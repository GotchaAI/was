package socket_server.domain.game.service;


import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;
import socket_server.domain.game.util.WordUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundService {
    /**
     * 도메인 데이터 Round 처리 담당.
     */
    private final RoundRepository roundRepository;
    private final GameRepository gameRepository;


    /**
     * START
     */
    public List<Round> initRounds(int totalRounds, List<GamePlayer> gamePlayers) {
        List<Round> rounds = new ArrayList<>();
        List<Integer> indexes = WordUtils.getRandomIndexes(totalRounds * 2); // get random indexes, 플레이어는 항상 2명이라고 가정
        for(int i = 0; i < totalRounds; i++) {
            List<Word> words = new ArrayList<>();
            for(int j = 0; j < 2; j++){
                Word word = Word.builder()
                        .wordIndex(j)
                        .word(WordUtils.getEngWord(indexes.get(i * 2 + j)))
                        .drawerUuid(gamePlayers.get(j).getPlayerUuid())
                        .aiGuesses(new ArrayList<>())
                        .playerGuesses(new ArrayList<>())
                        .aiPredictions(new ArrayList<>()).build();
                words.add(word);
            }

            Round round = Round.builder().
                    roundIndex(i + 1).
                    currentWordIndex(0).
                    words(words).
                    build();
            rounds.add(round);
        }
        return rounds;
    }

    private int getCurrentRoundIndex(String roomId){
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        return gameMeta.getCurrentRound();
    }

    public boolean checkAllDrawingSubmitted(String roomId){
        int currentRound = getCurrentRoundIndex(roomId);
        List<WordMeta> wordMetas = roundRepository.findWordMetas(roomId, currentRound);
        return wordMetas.stream().allMatch(WordMeta::isSubmitted);
    }

    public List<WordMeta> getWordMetas(String roomId){
        int currentRound = getCurrentRoundIndex(roomId);
        return roundRepository.findWordMetas(roomId, currentRound);
    }


    public void submitDrawing(String roomId, String drawerUuid, String imageURL) {
        // 0. 게임 메타정보 조회
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.DRAWING_SUBMIT)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        // 1. current round 가져오기
        int currentRound = getCurrentRoundIndex(roomId);

        // 2. word 찾고 업데이트
        List<WordMeta> wordMetas = findAndUpdateWordMeta(roomId, currentRound, drawerUuid, imageURL);

        // 3. 저장
        roundRepository.saveWordMetas(roomId, currentRound, wordMetas);
    }

    private List<WordMeta> findAndUpdateWordMeta(String roomId, int roundIndex, String drawerUuid, String imageURL) {
        List<WordMeta> wordMetas = roundRepository.findWordMetas(roomId, roundIndex);

        WordMeta targetWord = wordMetas.stream()
                .filter(word -> word.getDrawerUuid().equals(drawerUuid))
                .findFirst()
                .orElseThrow(() -> new CustomException(GameExceptionCode.INVALID_DRAWER_ID));

        if (targetWord.isSubmitted()) {
            throw new CustomException(GameExceptionCode.DRAWING_ALREADY_SUBMITTED);
        }

        targetWord.setImageURL(imageURL);
        targetWord.setSubmitted(true);

        return wordMetas;
    }

    public Round getCurrentRound(String roomId) {
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
