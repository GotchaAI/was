package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.util.JsonSerializer;


@Slf4j
@Repository
public class RoundRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public RoundRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
    public void saveRoundMetasString(String roomId, String roundsJson) {
        String key = getGameRoundsKey(roomId);
        redisTemplate.opsForValue().set(key, roundsJson);
        log.info("RoundMetas {} saved", roundsJson);
    }

    /**
     * Round 메타정보 List 조회 (roundIndex, drawingEndTime, roundWinner)
     */
    public String findRoundMetasString(String roomId) {
        String key = getGameRoundsKey(roomId);
        return redisTemplate.opsForValue().get(key);
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
    public void saveWordMetasString(String roomId, int roundIndex, String wordsJson) {
        String key = getRoundWordsKey(roomId, roundIndex);
        redisTemplate.opsForValue().set(key, wordsJson);
        log.info("Words {} saved", wordsJson);
    }

    /**
     * Word 메타정보 List 조회(wordIndex, word, drawerUuid, imageURL)
     */
    public String findWordMetasString(String roomId, int roundIndex) {
        String key = getRoundWordsKey(roomId, roundIndex);
        return redisTemplate.opsForValue().get(key);
    }



    private String getAIPredicionsKey(String roomId, int roundIndex, int wordIndex) {
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":ai_predictions";
    }

    /**
     * AI Prediction 정보 저장(top3 class, confidence)
     */
    public void saveAIPredictionsString(String roomId, int roundIndex, int wordIndex, String predictionsJson){
        String key = getAIPredicionsKey(roomId, roundIndex, wordIndex);
        redisTemplate.opsForValue().set(key, predictionsJson);
    }

    /**
     * AI Prediction 정보 조회(top3 class, confidence)
     */
    public String findAIPredictionsString(String roomId, int roundIndex, int wordIndex) {
        return redisTemplate.opsForValue().get(getAIPredicionsKey(roomId, roundIndex, wordIndex));
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
