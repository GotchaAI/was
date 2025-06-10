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
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class GameRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    public GameRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    /**
     * game:{roomId}
     */
    public static String getGameKey(String roomId) {
        return "game:" + roomId;
    }

    /**
     * Game 메타데이터만 저장(List GamePlayers, List Rounds 제외)
     */
    public void saveGameMeta(GameMeta gameMeta) {
        String redisKey = getGameKey(gameMeta.getRoomId());   // Redis 해시 키
        String lockKey = "lock:" + redisKey; // 락 키

        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                Map<String, Object> gameData = Map.of(
                        "gameType", gameMeta.getGameType().name(),
                        "difficulty", gameMeta.getDifficulty().name(),
                        "currentRound", String.valueOf(gameMeta.getCurrentRound()),
                        "gameStatus", String.valueOf(gameMeta.getGameStatus()),
                        "totalRounds", String.valueOf(gameMeta.getTotalRounds()),
                        "aiScore", String.valueOf(gameMeta.getAiScore()),
                        "playerScore", String.valueOf(gameMeta.getPlayerScore()),
                        "playerWon", String.valueOf(gameMeta.getPlayerWon())
                );
                redisTemplate.opsForHash().putAll(getGameKey(gameMeta.getRoomId()), gameData);
            } else {
                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //
            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * Game 메타데이터만 조회
     */
    public Map<Object, Object> findGameMeta(String roomId) {
        String redisKey = getGameKey(roomId);
        String lockKey = "lock:" + redisKey;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(1, 5, TimeUnit.SECONDS)) { // 1초 기다리고, 5초 동안 유지
                return redisTemplate.opsForHash().entries(redisKey);
            } else {
                throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_ACQUISITION_FAILED);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //
            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.LOCK_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void deleteGameMeta(String roomId){
        String redisKey = getGameKey(roomId);
        redisTemplate.delete(redisKey);
    }
}

