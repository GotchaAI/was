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

    //todo: game:{roomId}:players 는 SET,
    //todo: player:{roomId}:{uuid} 는 STRING (uuid, USERNAME)

    /**
     * game:{roomId}:players
     */
    public static String getGamePlayersKey(String roomId) {
        return GameRepository.getGameKey(roomId) + ":players";
    }

    /**
     * GamePlayers 저장
     */
    public void savePlayerUuids(String roomId, List<String> playerUuids) {
        String key = getGamePlayersKey(roomId);
        redisTemplate.opsForSet().add(key, playerUuids.toArray(new String[0]));
    };

    /**
     * GamePlayers 조회
     */
    public List<String> findPlayerUuidsByRoomId(String roomId) {
        String key = getGamePlayersKey(roomId);
        Set<String> uuids = redisTemplate.opsForSet().members(key);
        if (uuids == null || uuids.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(uuids);
    }

    /**
     * player:{roomId}:{uuid}
     */
    public static String getPlayerKey(String roomId, String uuid) {
        return GameRepository.getGameKey(roomId) + ":" + uuid;
    }

    public String findGamePlayerStringByUuid(String roomId, String uuid) {
        String key = getPlayerKey(roomId, uuid);
        return redisTemplate.opsForValue().get(key);
    }

    public void saveGamePlayerString(String roomId, String playerUuid, String playerJson) {
        String key = getPlayerKey(roomId, playerUuid);
        redisTemplate.opsForValue().set(key, playerJson);
        log.info("Player {} saved", playerJson);
    }




}