package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomRepository;
import socket_server.domain.room.repository.RoomUserRepository;

import java.util.List;
import java.util.Map;

import static socket_server.common.constants.WebSocketConstants.ROOM_JOIN;

@Service
@Slf4j
public class RoomUserService {

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;

    public RoomUserService( RedisTemplate<String, Object> objectRedisTemplate,
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

    public void broadcastUserList(String roomId, String userId){
        // 그 방에 누가 있는지 조회 후
        List<RoomUserInfo> userList = roomUserRepository.findUsersByRoomId(roomId);

        // 해당 방에 누가 있는지를 BroadCast
        objectRedisTemplate.convertAndSend(ROOM_JOIN+roomId,
                new RedisMessage(
                        userId,
                        ROOM_JOIN+roomId,
                        jsonSerializer.serialize(userList)));

        log.debug("Broadcasted user list to room {} by user {}", roomId, userId);
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

}

