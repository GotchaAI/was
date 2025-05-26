package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomRepository;
import socket_server.domain.room.repository.RoomUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static socket_server.common.constants.WebSocketConstants.ROOM_EVENT;

@Service
@Slf4j
public class RoomUserService {

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;

    public RoomUserService(RedisTemplate<String, Object> objectRedisTemplate,
                           RoomUserRepository roomUserRepository,
                           JsonSerializer jsonSerializer,
                           RoomRepository roomRepository) {
        this.jsonSerializer = jsonSerializer;
        this.roomUserRepository = roomUserRepository;
        this.objectRedisTemplate = objectRedisTemplate;
        this.roomRepository = roomRepository;
    }

    public void joinAndBroadcast(String roomId, String userUuid, String nickname, String password) {
        joinRoom(roomId, userUuid, nickname, password);
        broadcastUserList(roomId, userUuid);
    }

    public void updatePlayerReady(String roomId, String userUuid, boolean isReady) {
        RoomUserInfo userInfo = roomUserRepository.findUserInfoInRoom(roomId, userUuid);

        if (userInfo == null) {
            throw new CustomException(RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        userInfo.setReady(isReady);
        roomUserRepository.saveUserToRoom(userInfo, roomId);
        broadcastReadyStatus(roomId, userUuid, isReady);
    }

    public void exitRoom(String roomId, String userUuid) {
        roomUserRepository.removeUserFromRoom(roomId, userUuid);
        broadcastExit(roomId, userUuid);
    }

    public void joinRoom(String roomId, String userUuid, String nickname, String password) {
        checkUserNotInAnyRoom(userUuid); // after check not in any room

        validatePasswordIfRequired(roomId, password);

        RoomUserInfo roomUserInfo = RoomUserInfo.builder().
                userUuid(userUuid).
                nickname(nickname).
                ready(false).
                build();

        roomUserRepository.saveUserToRoom(roomUserInfo, roomId);
        log.info("User {} joined room {}", userUuid, roomId);
    }

    public void checkUserNotInAnyRoom(String userUuid) {
        String value = roomUserRepository.findRoomIdByUserUuid(userUuid);
        if (value != null) {
            throw new CustomException(RoomExceptionCode.USER_ALREADY_IN_ANOTHER_ROOM);
        }
    }


    private void validatePasswordIfRequired(String roomId, String password) {
        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
        if (roomData == null || roomData.isEmpty()) {
            throw new CustomException(RoomExceptionCode.INVALID_ROOM_ID);
        }

        if ("true".equals(roomData.get("hasPassword"))) {
            String expectedPassword = (String) roomData.get("password");
            if (expectedPassword == null || !expectedPassword.equals(password)) {
                throw new CustomException(RoomExceptionCode.INCORRECT_PASSWORD);
            }
        }
    }

    public String findRoomIdByUserUuid(String userUuid) {
        return roomUserRepository.findRoomIdByUserUuid(userUuid);
    }

    public void validateRoomUser(String roomId, String userUuid) {
        //방이 실존하는지 확인
        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
        if (roomData == null || roomData.isEmpty()) {
            throw new CustomException(RoomExceptionCode.INVALID_ROOM_ID);
        }

        // 유저가 방에 실제 존재하는지 확인
        RoomUserInfo userInfo = roomUserRepository.findUserInfoInRoom(roomId, userUuid);
        if (userInfo == null) {
            throw new CustomException(RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        // 방장이 맞는지 확인
        String ownerUuid = (String) roomData.get(RoomField.OWNER_UUID.getRedisField());
        if (!userUuid.equals(ownerUuid)) {
            throw new CustomException(RoomExceptionCode.NOT_ROOM_OWNER);
        }
    }

    private void broadcastUserList(String roomId, String userId){
        List<RoomUserInfo> userList = roomUserRepository.findUsersByRoomId(roomId);
        broadcastToRoom(roomId, userId, EventType.JOIN, userList);
    }

    private void broadcastReadyStatus(String roomId, String userUuid, boolean isReady) {
        broadcastToRoom(roomId, userUuid, isReady ? EventType.READY : EventType.UNREADY, userUuid);
    }


    private void broadcastExit(String roomId, String userUuid) {
        broadcastToRoom(roomId, userUuid, EventType.EXIT, userUuid);
    }

    private void broadcastToRoom(String roomId, String senderId, EventType type, Object data) {
        EventRes eventRes = new EventRes(
                type,
                data,
                LocalDateTime.now()
        );

        objectRedisTemplate.convertAndSend(
                ROOM_EVENT + roomId,
                new RedisMessage(
                        senderId,
                        ROOM_EVENT + roomId,
                        jsonSerializer.serialize(eventRes)
                )
        );

        log.debug("Broadcasted {} event in room {} from user {}", type, roomId, senderId);
    }
}

