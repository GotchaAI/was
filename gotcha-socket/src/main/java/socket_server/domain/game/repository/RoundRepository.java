package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;

import java.util.concurrent.TimeUnit;


@Slf4j
@Repository
public class RoundRepository {
    private final RedisTemplate<String, String> redisTemplate;
    //todo: Lock 관련 코드 전면 수정
    private final RedissonClient redissonClient;

    public RoundRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
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
        String redisKey = getGameRoundsKey(roomId);
//        String lockKey = "lock:" + redisKey;
//
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                redisTemplate.opsForValue().set(redisKey, roundsJson);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    /**
     * Round 메타정보 List 조회 (roundIndex, drawingEndTime, roundWinner)
     */
    public String findRoundMetasString(String roomId) {
        String key = getGameRoundsKey(roomId);
//        String lockKey = "lock:" + key;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                return redisTemplate.opsForValue().get(key);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }


    public void deleteRoundMetas(String roomId) {
        String key = getGameRoundsKey(roomId);
        redisTemplate.delete(key);
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
        String redisKey = getRoundWordsKey(roomId, roundIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                redisTemplate.opsForValue().set(redisKey, wordsJson);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    /**
     * Word 메타정보 List 조회(wordIndex, word, drawerUuid, imageURL)
     */
    public String findWordMetasString(String roomId, int roundIndex) {
        String redisKey = getRoundWordsKey(roomId, roundIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                return redisTemplate.opsForValue().get(redisKey);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    public void deleteWordMetas(String roomId, int roundIndex) {
        String key = getRoundWordsKey(roomId, roundIndex);
        redisTemplate.delete(key);
    }


    private String getAIPredicionsKey(String roomId, int roundIndex, int wordIndex) {
        return getRoundWordsKey(roomId, roundIndex) + ":" +wordIndex + ":ai_predictions";
    }

    /**
     * AI Prediction 정보 저장(top3 class, confidence)
     */
    public void saveAIPredictionsString(String roomId, int roundIndex, int wordIndex, String predictionsJson){
        String redisKey = getAIPredicionsKey(roomId, roundIndex, wordIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                redisTemplate.opsForValue().set(redisKey, predictionsJson);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    /**
     * AI Prediction 정보 조회(top3 class, confidence)
     */
    public String findAIPredictionsString(String roomId, int roundIndex, int wordIndex) {
        String redisKey = getAIPredicionsKey(roomId, roundIndex, wordIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                return redisTemplate.opsForValue().get(redisKey);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    public void deleteAIPredictions(String roomId, int roundIndex, int wordIndex) {
        String key = getAIPredicionsKey(roomId, roundIndex, wordIndex);
        redisTemplate.delete(key);
    }


    private String getAIGuessKey(String roomId, int roundIndex, int wordIndex){
        return getRoundWordsKey(roomId, roundIndex) + ":" + wordIndex + ":ai_guesses";
    }


    /**
     * AI Guess 정보 저장
     */
    public void saveAIGuessesString(String roomId, int roundIndex, int wordIndex, String guessesJson){
        String redisKey = getAIGuessKey(roomId, roundIndex, wordIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                redisTemplate.opsForValue().set(redisKey, guessesJson);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    /**
     * AI List Guess 조회
     */
    public String findAIGuessesString(String roomId, int roundIndex, int wordIndex) {
        String redisKey = getAIGuessKey(roomId, roundIndex, wordIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                return redisTemplate.opsForValue().get(redisKey);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    public void deleteAIGuesses(String roomId, int roundIndex, int wordIndex) {
        String key = getAIGuessKey(roomId, roundIndex, wordIndex);
        redisTemplate.delete(key);
    }




    private String getPlayerGuessKey(String roomId, int roundIndex, int wordIndex){
        return getRoundWordsKey(roomId, roundIndex) +":" + wordIndex + ":player_guesses";
    }

    /**
     * Player List Guess 조회
     */
    public String findPlayerGuessesString(String roomId, int roundIndex, int wordIndex) {
        String redisKey = getPlayerGuessKey(roomId, roundIndex, wordIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                return redisTemplate.opsForValue().get(redisKey);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    /**
     * Player Guess 정보 저장
     */
    public void savePlayerGuesses(String roomId, int roundIndex, int wordIndex, String guessesJson){
        String redisKey = getPlayerGuessKey(roomId, roundIndex, wordIndex);
//        String lockKey = "lock:" + redisKey;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                redisTemplate.opsForValue().set(redisKey, guessesJson);
//            } else {
//                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); //
//            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
//        } finally {
//            if (lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
//        }
    }

    public void deletePlayerGuesses(String roomId, int roundIndex, int wordIndex) {
        String key = getPlayerGuessKey(roomId, roundIndex, wordIndex);
        redisTemplate.delete(key);
    }

}
