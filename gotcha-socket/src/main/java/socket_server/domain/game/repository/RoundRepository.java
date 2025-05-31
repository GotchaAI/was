package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.AiPrediction;
import socket_server.domain.game.model.Guess;

import java.util.ArrayList;
import java.util.List;



@Slf4j
@Repository
public class RoundRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;

    public RoundRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                           JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    /**
     * game:{roomId}:rounds
     */
    public static String getGameRoundsKey(String roomId) {
        return GameRepository.getGameKey(roomId) + ":rounds";
    }

    /**
     * Round 메타정보 List 저장 (roundIndex, drawingEndTime, roundWinner)
     */
    public void saveRoundMetas(String roomId, List<RoundMeta> roundMetas, ErrorType errorType) {
        String key = getGameRoundsKey(roomId);
        String roundsJson = jsonSerializer.serialize(roundMetas, errorType);
        redisTemplate.opsForValue().set(key, roundsJson);
        log.info("RoundMetas {} saved", roundsJson);
    }

    /**
     * Round 메타정보 List 조회 (roundIndex, drawingEndTime, roundWinner)
     */
    public List<RoundMeta> findRoundMetas(String roomId, ErrorType errorType) {
        String key = getGameRoundsKey(roomId);
        String roundsJson = redisTemplate.opsForValue().get(key);
        if (roundsJson == null) {
            return new ArrayList<>();
        }
        return jsonSerializer.deserializeList(roundsJson, RoundMeta.class, errorType);
    }

    /**
     * game:{roomId}:round:{roundIndex}:words
     */
    private String getRoundWordsKey(String roomId, int roundIndex) {
        return getGameRoundsKey(roomId) + ":" + roundIndex + ":words";
    }

    /**
     * Word 메타정보 List 저장(wordIndex, word, drawerUuid)
     */
    public void saveWordMetas(String roomId, int roundIndex, List<WordMeta> wordMetas, ErrorType errorType) {
        String key = getRoundWordsKey(roomId, roundIndex);
        String wordsJson = jsonSerializer.serialize(wordMetas, errorType);
        redisTemplate.opsForValue().set(key, wordsJson);
        log.info("Words {} saved", wordsJson);
    }

    /**
     * Word 메타정보 List 조회(wordIndex, word, drawerUuid, imageURL)
     */
    public List<WordMeta> findWordMetas(String roomId, int roundIndex, ErrorType errorType) {
        String key = getRoundWordsKey(roomId, roundIndex);
        String wordsJson = redisTemplate.opsForValue().get(key);
        if (wordsJson == null) {
            return new ArrayList<>();
        }
        return jsonSerializer.deserializeList(wordsJson, WordMeta.class, errorType);
    }



    private String getAIPredicionsKey(String roomId, int roundIndex, int wordIndex) {
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":ai_predictions";
    }

    /**
     * AI Prediction 정보 저장(top3 class, confidence)
     */
    public void saveAIPredictions(String roomId, int roundIndex, int wordIndex, List<AiPrediction> predictions, ErrorType errorType){
        String key = getAIPredicionsKey(roomId, roundIndex, wordIndex);
        String predictionsJson = jsonSerializer.serialize(predictions, errorType);
        redisTemplate.opsForValue().set(key, predictionsJson);
        log.info("AI Predictions {} saved", predictionsJson);
    }

    /**
     * AI Prediction 정보 조회(top3 class, confidence)
     */
    public List<AiPrediction> findAIPredictions(String roomId, int roundIndex, int wordIndex, ErrorType errorType) {
        return jsonSerializer.deserializeList(redisTemplate.opsForValue().get(getAIPredicionsKey(roomId, roundIndex, wordIndex)), AiPrediction.class, errorType);
    }

    private String getAIGuessKey(String roomId, int roundIndex, int wordIndex){
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":ai_guesses";
    }


    private String getPlayerGuessKey(String roomId, int roundIndex, int wordIndex){
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":player_guesses";
    }



    /**
     * AI Guess 정보 저장
     */
    public void saveAIGuessesString(String roomId, int roundIndex, int wordIndex, String guessesJson){
        String key = getAIGuessKey(roomId, roundIndex, wordIndex);
        redisTemplate.opsForValue().set(key, guessesJson);
        log.info("AI Guesses {} saved", guessesJson);
    }

    /**
     * AI List Guess 조회
     */
    public String findAIGuessesString(String roomId, int roundIndex, int wordIndex) {
        String key = getAIGuessKey(roomId, roundIndex, wordIndex);
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * Player List Guess 조회
     */
    public String findPlayerGuessesString(String roomId, int roundIndex, int wordIndex) {
        String key = getPlayerGuessKey(roomId, roundIndex, wordIndex);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Player Guess 정보 저장
     */
    public void savePlayerGuesses(String roomId, int roundIndex, int wordIndex, String guessesJson){
        String key = getPlayerGuessKey(roomId, roundIndex, wordIndex);
        redisTemplate.opsForValue().set(key, guessesJson);
        log.info("Player Guesses {} saved", guessesJson);
    }


}
