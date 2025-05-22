package socket_server.domain.room.repository;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.model.RoomUserInfo;

import java.util.List;
import java.util.Map;

@Repository
public class RoomUserRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final JsonSerializer jsonSerializer;

    public RoomUserRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate, JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    private String roomUserKey(String roomId) {
        return "room:" + roomId + ":users";
    }

    private String userRoomKey(String userId) {
        return "user:" + userId + ":room";
    }

    public void saveUserToRoom(RoomUserInfo roomUserInfo, String roomId) {

        String parsedJson = jsonSerializer.serialize(roomUserInfo);
        /**
         * key : room:roomId:users
         * field: userId
         * value: RoomUserInfo
         * -------------------------
         * key: user:userId:room
         * string: roomId
         */
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.multi();
            connection.hashCommands().hSet(
                    roomUserKey(roomId).getBytes(),
                    roomUserInfo.getUserId().getBytes(),
                    parsedJson.getBytes());
            connection.stringCommands().set(userRoomKey(roomUserInfo.getUserId()).getBytes(), roomId.getBytes());
            return connection.exec();
        });
    }

    public List<RoomUserInfo> findUsersByRoomId(String roomId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(roomUserKey(roomId));
        return entries.values().stream().map(raw -> jsonSerializer.deserialize(raw, RoomUserInfo.class)).toList();
    }

    public void removeUserFromRoom(String roomId, String userId) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.multi();
            connection.sRem(roomUserKey(roomId).getBytes(), userId.getBytes());
            connection.sRem(userRoomKey(userId).getBytes(), roomId.getBytes());
            return connection.exec();
        });
    }

    public String findRoomIdByUserId(String userId) {
        return redisTemplate.opsForValue().get(userRoomKey(userId));
    }


}
