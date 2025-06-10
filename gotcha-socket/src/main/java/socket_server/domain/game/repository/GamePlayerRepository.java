package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class GamePlayerRepository {

    private final RedisTemplate<String, String> redisTemplate;
    public GamePlayerRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * game:{roomId}:players
     */
    public static String getGamePlayersKey(String roomId) {
        return GameRepository.getGameKey(roomId) + ":players";
    }

    /**
     * GamePlayers 저장
     */
    public void savePlayersString(String roomId, String playersJson) {
        String key = getGamePlayersKey(roomId);
        redisTemplate.opsForValue().set(key, playersJson);
    };

    /**
     * GamePlayers 조회
     */
    public String findPlayersStringByRoomId(String roomId) {
        String key = getGamePlayersKey(roomId);
        return redisTemplate.opsForValue().get(key);
    }

}