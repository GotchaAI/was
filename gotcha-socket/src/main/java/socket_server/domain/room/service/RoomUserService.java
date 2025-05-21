package socket_server.domain.room.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gotcha_common.exception.CustomException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomUserRepository;

import java.util.*;

import static socket_server.common.constants.WebSocketConstants.ROOM_JOIN;

@Service
public class RoomUserService {

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;
    private final RoomUserRepository roomUserRepository;

    public RoomUserService( RedisTemplate<String, Object> objectRedisTemplate, RoomUserRepository roomUserRepository, JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
        this.roomUserRepository = roomUserRepository;
        this.objectRedisTemplate = objectRedisTemplate;
    }

    public void joinRoom(RoomUserInfo roomUserInfo, String roomId) {
        checkUserNotInAnyRoom(roomUserInfo.getUserId()); // after check not in any room
        roomUserRepository.saveUserToRoom(roomUserInfo, roomId);
    }

    public void broadcastUserList(String roomId, String userId){
        // 그 방에 누가 있는지 조회 후
        List<RoomUserInfo> userList = roomUserRepository.findUsersByRoomId(roomId);

        // 해당 방에 누가 있는지를 BroadCast
        objectRedisTemplate.convertAndSend(ROOM_JOIN+roomId,
                new RedisMessage(
                        userId,
                        ROOM_JOIN+roomId,
                        jsonSerializer.serialize(userList)));

    }

    public void checkUserNotInAnyRoom(String userId) {
        String value = roomUserRepository.findRoomIdByUserId(userId);
        if (value != null) {
            throw new CustomException(RoomExceptionCode.USER_ALREADY_IN_ANOTHER_ROOM);
        }
    }
}

