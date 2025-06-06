package socket_server.domain.game.repository;

import gotcha_common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.*;
import socket_server.domain.game.meta.RoundMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class GameRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public GameRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
    public void saveGameMeta(GameMeta gameMeta){
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

//        log.info("Game {} saved", gameData);
    }

    /**
     * Game 메타데이터만 조회(List GamePlayers, List Rounds 제외)
     */
    public Map<Object, Object> findGameMeta(String roomId) {
        String key = getGameKey(roomId);
        return redisTemplate.opsForHash().entries(key);
    }





}
