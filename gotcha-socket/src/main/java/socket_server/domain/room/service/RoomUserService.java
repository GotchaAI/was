package socket_server.domain.room.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import gotcha_user.dto.UserInfoRes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.domain.room.model.RoomUserInfo;

import java.util.*;

@Service
public class RoomUserService {

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    public RoomUserService(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    private String roomUserKey(String roomId) {
        return "room:" + roomId + ":users";
    }

    private String userRoomKey(String userId) {
        return "user:" + userId + ":room";
    }

    public void joinRoom(RoomUserInfo roomUserInfo, String roomId) throws JsonProcessingException {
        checkUserNotInAnyRoom(roomUserInfo.getUserId()); // after check not in any room

        String parsedJson = objectMapper.writeValueAsString(roomUserInfo);

        // RoomUserInfo Redis에 저장
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

    public void leaveRoom(String userId, String roomId) {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.multi();
            connection.sRem(roomUserKey(roomId).getBytes(), userId.getBytes());
            connection.sRem(userRoomKey(userId).getBytes(), roomId.getBytes());
            return connection.exec();
        });
    }


    public boolean isUserInRoom(String userId, String roomId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(roomUserKey(roomId), userId));
    }

    public List<RoomUserInfo> getUsersInRoom(String roomId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(roomUserKey(roomId));

        return entries.values().stream().map(string -> {
            try {
                return objectMapper.readValue((String) string, RoomUserInfo.class);
            } catch (JsonProcessingException e) {
                throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
            }
        }).toList();
    }

    public void checkUserNotInAnyRoom(String userId) {
        String key = userRoomKey(userId); // 예: user:{uuid}:room
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            throw new CustomException(RoomExceptionCode.USER_ALREADY_IN_ANOTHER_ROOM);
        }

    }
}

