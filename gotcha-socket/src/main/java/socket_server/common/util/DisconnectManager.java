package socket_server.common.util;

import gotcha_common.exception.CustomException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import socket_server.common.exception.room.RoomExceptionCode;

import java.util.concurrent.TimeUnit;

@Component
public class DisconnectManager {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "disconnect:room:";

    public DisconnectManager(@Qualifier("socketStringRedisTemplate")
                             RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void markDisconnect(String userUuid, String roomId) {
        redisTemplate.opsForValue().set(PREFIX + userUuid, roomId, 30, TimeUnit.SECONDS);
    }

    public String cancelDisconnectIfExists(String userUuid) {
        String key = PREFIX + userUuid;
        String roomId = redisTemplate.opsForValue().get(key);
        if (roomId == null) {
            throw new CustomException(RoomExceptionCode.RECONNECT_ROOM_EXPIRED);
        }
        redisTemplate.delete(key);
        return roomId;
    }
}

