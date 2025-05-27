package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuessFlowService {
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final GuessRequestService guessRequestService;
    /**
     * GUESS_START (ENTRY_POINT)
     */
    public void startGuessing(String roomId) {
        // 0. 게임 메타정보 조회 -> GameStatus 업데이트
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_START)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }
        gameMeta.setGameStatus(GameStatus.GUESSING_PHASE);
        gameRepository.saveGameMeta(gameMeta);

        // 1. 라운드 데이터 전체 조회
        Round currentRound = getCurrentRound(roomId);

        // 2. WordMeta BroadCast (현재 라운드에 대해서)
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_START, currentRound.getWords().stream().map(Word::toWordMeta).toList());

        // todo: GUESS_REQUEST(EntryPoint)

        // 3. 현재 라운드에서, Guess 해야하는 Word 찾음
        Word guessTargetWord = currentRound.getWords().get(currentRound.getCurrentWordIndex());


        // 4. request Guess to AI
        guessRequestService.requestGuessAI(roomId, currentRound, guessTargetWord);
    }


    /**
     * 다음 추측을 진행. 흐름 제어용 코드
     */
    public void processNextGuess(String roomId){
        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(currentRound);
        if(currentWord== null){
            // todo: next round

            return;
        }




    }


    /**
     * 단어 추측 완료 여부 확인
     */
    private boolean isWordGuessCompleted(Word word) {
        // 1. 누군가 맞췄는지 확인
        boolean hasCorrectGuess = word.getAiGuesses().stream().anyMatch(Guess::getCorrect) ||
                word.getPlayerGuesses().stream().anyMatch(Guess::getCorrect);
        if (hasCorrectGuess) return true;

        // 2. AI와 플레이어 모두 3번씩 시도했는지 확인
        int aiAttempts = word.getAiGuesses().size();
        int playerAttempts = word.getPlayerGuesses().size();

        return aiAttempts >= 3 && playerAttempts >= 3;
    }



    /**
     * 현재 추측할 단어 가져오기
     */
    private Word getCurrentWord(Round round) {
        if (round.getCurrentWordIndex() >= round.getWords().size()) {
            return null; // 모든 단어 완료
        }
        return round.getWords().get(round.getCurrentWordIndex());
    }






















    private int getCurrentRoundIndex(String roomId){
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        return gameMeta.getCurrentRound();
    }

    private Round getCurrentRound(String roomId) {
        // 1. gameMeta 조회 후 현재 라운드 index 받기
        int roundIndex = getCurrentRoundIndex(roomId);

        // 2. Round 메타정보 조회
        RoundMeta roundMeta = roundRepository.findRoundMetas(roomId).get(roundIndex - 1);

        // 3. Words 메타정보 조회
        List<WordMeta> wordMetas = roundRepository.findWordMetas(roomId, roundIndex);

        // 4. 데이터 파싱 후 결합
        List<Word> words = wordMetas.stream().map(WordMeta::toWord).toList();
        Round round = RoundMeta.toRound(roundMeta);
        round.setWords(words);

        return round;
    }
}
