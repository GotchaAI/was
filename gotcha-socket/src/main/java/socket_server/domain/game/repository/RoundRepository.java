package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;

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
            return List.of();
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
            return List.of();
        }
        return jsonSerializer.deserializeList(wordsJson, WordMeta.class);
    }


    private String getGuessKey(String roomId, int roundIndex, int wordIndex){
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":guesses";
    }

    /**
     * GUESS 정보 추가
     */
    public void addGuess(String roomId, int roundIndex, int wordIndex, Guess guess){
        String key = getGuessKey(roomId, roundIndex, wordIndex);
        String guessJson = jsonSerializer.serialize(guess);
        redisTemplate.opsForList().rightPush(key, guessJson);
        log.info("Guess {} saved", guessJson);
    }

    /**
     * List Guess 조회
     */
    public List<Guess> findGuesses(String roomId, int roundIndex, int wordIndex){
        String key = getGuessKey(roomId, roundIndex, wordIndex);
        List<String> guessesJsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (guessesJsonList == null || guessesJsonList.isEmpty()) {
            return List.of();
        }
        return jsonSerializer.deserializeList(guessesJsonList, Guess.class);
    }

}
