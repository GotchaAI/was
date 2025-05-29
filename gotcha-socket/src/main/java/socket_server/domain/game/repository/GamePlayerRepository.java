package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.model.GamePlayer;

import java.util.List;

@Slf4j
@Repository
public class GamePlayerRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;
    public GamePlayerRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                                JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
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
    public void savePlayers(String roomId, List<GamePlayer> players) {
        String key = getGamePlayersKey(roomId);
        String playersJson = jsonSerializer.serialize(players, ErrorType.GAME);
        redisTemplate.opsForValue().set(key, playersJson);
        log.info("Players {} saved", playersJson);
    }

    /**
     * GamePlayers 조회
     */
    public List<GamePlayer> findPlayers(String roomId) {
        String key = getGamePlayersKey(roomId);
        String playersJson = redisTemplate.opsForValue().get(key);
        if (playersJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(playersJson, GamePlayer.class, ErrorType.GAME);
    }


}
