package socket_server.domain.room.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void updateAllFields(String roomId, Map<String, String> updates) {
        redisTemplate.opsForHash().putAll(getRoomKey(roomId), updates);
    }

    public void deleteRoom(String roomId) {
        redisTemplate.delete(getRoomKey(roomId));
    }

    /**
     * SCAN 명령을 사용하여 서버를 블로킹하지 않고 안전하게 room 키들을 순회합니다.
     * @return room 키들(예: "room:1234")을 순회할 수 있는 Cursor 객체
     */
    public Cursor<String> scanRoomKeys() {
        ScanOptions options = ScanOptions.scanOptions().match("room:*").count(1000).build();
        return redisTemplate.scan(options);
    }

    /**
     * @deprecated 이 메서드는 KEYS 명령을 사용하므로 운영 환경에서는 적합하지 않음
     * 대신 scanRoomKeys() 메서드 사용
     */
    @Deprecated
    private Set<String> getAllRoomIds() {
        Set<String> keys = redisTemplate.keys("room:*");
        if (keys == null) return Set.of();

        return keys.stream()
                .filter(k -> k.matches("room:[^:]+"))
                .map(k -> k.replaceFirst("room:", ""))
                .collect(Collectors.toSet());
    }
}
