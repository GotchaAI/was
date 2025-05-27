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
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuessStartService {
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final GuessRequestService guessRequestService;
    /**
     * GUESS_START
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
        Round round = getCurrentRound(roomId);

        // 2. WordMeta BroadCast (현재 라운드에 대해서)
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_START, round.getWords().stream().map(Word::toWordMeta).toList());

        // todo: GUESS_REQUEST(EntryPoint)

        // 3. 현재 라운드에서, Guess 해야하는 Word 찾음
        Word guessTargetWord = round.getWords().get(round.getCurrentWordIndex());


        // 4. request Guess to AI
        guessRequestService.requestGuessAI(roomId, round, guessTargetWord);
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
