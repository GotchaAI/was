package socket_server.domain.room.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomUserService {

    private final RedisTemplate<String, String> redisTemplate;

    public RoomUserService(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String roomUserKey(String roomId) {
        return "room:" + roomId + ":users";
    }

    private String userRoomKey(String userId) {
        return "user:" + userId + ":rooms";
    }

    private String userCurrentRoomKey(String userId) {
        return "user:" + userId + ":currentRoom";
    }

    public void joinRoom(String userId, String roomId) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.multi();
            connection.sAdd(roomUserKey(roomId).getBytes(), userId.getBytes());
            connection.sAdd(userRoomKey(userId).getBytes(), roomId.getBytes());
            connection.set(userCurrentRoomKey(userId).getBytes(), roomId.getBytes());
            return connection.exec();
        });
    }

    public void leaveRoom(String userId, String roomId) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.multi();
            connection.sRem(roomUserKey(roomId).getBytes(), userId.getBytes());
            connection.sRem(userRoomKey(userId).getBytes(), roomId.getBytes());
            connection.del(userCurrentRoomKey(userId).getBytes());
            return connection.exec();
        });
    }

    public boolean isUserInRoom(String userId, String roomId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(roomUserKey(roomId), userId));
    }

    public List<String> getUsersInRoom(String roomId) {
        return new ArrayList<>(redisTemplate.opsForSet().members(roomUserKey(roomId)));
    }

    public Optional<String> getCurrentRoomOfUser(String userId) {
        String value = redisTemplate.opsForValue().get(userCurrentRoomKey(userId));
        return Optional.ofNullable(value);
    }
}

