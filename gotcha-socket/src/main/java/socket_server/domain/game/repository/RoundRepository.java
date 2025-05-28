package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
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
    public void saveRoundMetas(String roomId, List<RoundMeta> roundMetas) {
        String key = getGameRoundsKey(roomId);
        String roundsJson = jsonSerializer.serialize(roundMetas);
        redisTemplate.opsForValue().set(key, roundsJson);
        log.info("RoundMetas {} saved", roundsJson);
    }

    /**
     * Round 메타정보 List 조회 (roundIndex, drawingEndTime, roundWinner)
     */
    public List<RoundMeta> findRoundMetas(String roomId) {
        String key = getGameRoundsKey(roomId);
        String roundsJson = redisTemplate.opsForValue().get(key);
        if (roundsJson == null) {
            return new ArrayList<>();
        }
        return jsonSerializer.deserializeList(roundsJson, RoundMeta.class);
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
    public void saveWordMetas(String roomId, int roundIndex, List<WordMeta> wordMetas) {
        String key = getRoundWordsKey(roomId, roundIndex);
        String wordsJson = jsonSerializer.serialize(wordMetas);
        redisTemplate.opsForValue().set(key, wordsJson);
        log.info("Words {} saved", wordsJson);
    }

    /**
     * Word 메타정보 List 조회(wordIndex, word, drawerUuid, imageURL)
     */
    public List<WordMeta> findWordMetas(String roomId, int roundIndex) {
        String key = getRoundWordsKey(roomId, roundIndex);
        String wordsJson = redisTemplate.opsForValue().get(key);
        if (wordsJson == null) {
            return new ArrayList<>();
        }
        return jsonSerializer.deserializeList(wordsJson, WordMeta.class);
    }



    private String getAIPredicionsKey(String roomId, int roundIndex, int wordIndex) {
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":ai_predictions";
    }

    /**
     * AI Prediction 정보 저장(top3 class, confidence)
     */
    public void saveAIPredictions(String roomId, int roundIndex, int wordIndex, List<AiPrediction> predictions){
        String key = getAIPredicionsKey(roomId, roundIndex, wordIndex);
        String predictionsJson = jsonSerializer.serialize(predictions);
        redisTemplate.opsForValue().set(key, predictionsJson);
        log.info("AI Predictions {} saved", predictionsJson);
    }

    /**
     * AI Prediction 정보 조회(top3 class, confidence)
     */
    public List<AiPrediction> findAIPredictions(String roomId, int roundIndex, int wordIndex) {
        return jsonSerializer.deserializeList(redisTemplate.opsForValue().get(getAIPredicionsKey(roomId, roundIndex, wordIndex)), AiPrediction.class);
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
    public void saveAIGuesses(String roomId, int roundIndex, int wordIndex, List<Guess> guesses){
        String key = getAIGuessKey(roomId, roundIndex, wordIndex);
        String guessesJson = jsonSerializer.serialize(guesses);
        redisTemplate.opsForValue().set(key, guessesJson);
        log.info("AI Guesses {} saved", guessesJson);
    }

    /**
     * AI List Guess 조회
     */
    public List<Guess> findAIGuesses(String roomId, int roundIndex, int wordIndex){
        String key = getAIGuessKey(roomId, roundIndex, wordIndex);
        String guessesJsonList = redisTemplate.opsForValue().get(key);
        if (guessesJsonList == null || guessesJsonList.isEmpty()) {
            return new ArrayList<>();
        }
        return jsonSerializer.deserializeList(guessesJsonList, Guess.class);
    }


    /**
     * Player List Guess 조회
     */
    public List<Guess> findPlayerGuesses(String roomId, int roundIndex, int wordIndex) {
        String key = getPlayerGuessKey(roomId, roundIndex, wordIndex);
        String guessesJsonList = redisTemplate.opsForValue().get(key);
        if (guessesJsonList == null || guessesJsonList.isEmpty()) {
            return new ArrayList<>();
        }
        return jsonSerializer.deserializeList(guessesJsonList, Guess.class);
    }

    /**
     * Player Guess 정보 저장
     */
    public void savePlayerGuesses(String roomId, int roundIndex, int wordIndex, List<Guess> guesses){
        String key = getPlayerGuessKey(roomId, roundIndex, wordIndex);
        String guessesJson = jsonSerializer.serialize(guesses);
        redisTemplate.opsForValue().set(key, guessesJson);
        log.info("Player Guesses {} saved", guessesJson);
    }
}
