package socket_server.domain.room.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class RoomRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RoomRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRoomData(String roomId, Map<String, String> roomData) {
        redisTemplate.opsForHash().putAll(getRoomKey(roomId), roomData);
    }

    public Map<Object, Object> getRoomData(String roomId) {
        return redisTemplate.opsForHash().entries(getRoomKey(roomId));
    }

    private String getRoomKey(String roomId) {
        return "room:" + roomId;
    }
}