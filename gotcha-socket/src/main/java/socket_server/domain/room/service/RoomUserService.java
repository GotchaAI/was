package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomJoinRes;
import socket_server.domain.room.dto.RoomSummaryRes;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomRepository;
import socket_server.domain.room.repository.RoomUserRepository;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RoomUserService {

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;
    private final RoomBroadcaster roomBroadcaster;

    public RoomUserService(RedisTemplate<String, Object> objectRedisTemplate,
                           RoomUserRepository roomUserRepository,
                           JsonSerializer jsonSerializer,
                           RoomRepository roomRepository,
                           RoomBroadcaster roomBroadcaster) {
        this.jsonSerializer = jsonSerializer;
        this.roomUserRepository = roomUserRepository;
        this.objectRedisTemplate = objectRedisTemplate;
        this.roomRepository = roomRepository;
        this.roomBroadcaster = roomBroadcaster;
    }

    public void joinAndBroadcast(String roomId, String userUuid, String nickname, String password) {
        joinRoom(roomId, userUuid, nickname, password);
        broadcastRoomInfo(roomId, userUuid);
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
        boolean isOwner = validateRoomOwner(roomId, userUuid);

        roomUserRepository.removeUserFromRoom(roomId, userUuid);
        broadcastExit(roomId, userUuid);

        if (isOwner) {
            List<RoomUserInfo> remainingUsers = roomUserRepository.findUsersByRoomId(roomId);

            if (!remainingUsers.isEmpty()) {
                RoomUserInfo newOwner = remainingUsers.get(0);
                passRoomOwner(roomId, newOwner);
            } else {
                log.info("방 {}에 유저가 없어 방을 삭제합니다.", roomId);
                roomRepository.deleteRoom(roomId);
                roomUserRepository.deleteUserList(roomId);
                roomBroadcaster.broadcastToRoom(roomId, "SYSTEM", EventType.DELETE, "방이 삭제되었습니다");
            }
        }
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

    public void checkGameStart(String roomId, GameType gameType) {
        List<RoomUserInfo> users = roomUserRepository.findUsersByRoomId(roomId);
        for(RoomUserInfo user : users) {
            if(!user.isReady()) {
                throw new CustomException(RoomExceptionCode.NOT_ALL_PLAYER_READY);
            }
        }

        if(gameType.equals(GameType.TRICK_MYOMYO)) {
            if(users.size() != 2) {
                throw new CustomException(RoomExceptionCode.INVALID_GAME_PLAYERS);
            }
        }
        else {
            if(users.size() != 1) {
                throw new CustomException(RoomExceptionCode.INVALID_GAME_PLAYERS);
            }
        }
    }


    public RoomMetadata validateRoomOwnerAndGetRoomMetadata(String roomId, String userUuid) {
        if(!validateRoomOwner(roomId, userUuid)){
            throw new CustomException(RoomExceptionCode.NOT_ROOM_OWNER);
        }

        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
        return RoomMetadata.fromRedisMap(roomId, roomData);
    }

    public boolean validateRoomOwner(String roomId, String userUuid) {
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
            return false;
        }

        return true;
    }

    private void broadcastRoomInfo(String roomId, String userId){
        List<RoomUserInfo> userList = roomUserRepository.findUsersByRoomId(roomId);
        RoomMetadata roomMetadata = RoomMetadata.fromRedisMap(roomId, roomRepository.getRoomData(roomId));

        RoomJoinRes roomJoinRes = new RoomJoinRes(roomMetadata, userList);
        roomBroadcaster.broadcastToRoom(roomId, userId, EventType.JOIN, roomJoinRes);

        RoomSummaryRes roomSummaryRes = RoomSummaryRes.of(roomMetadata, userList.size());
        roomBroadcaster.broadcastToRoomList("SYSTEM", EventType.UPDATE, roomSummaryRes);
    }

    private void broadcastReadyStatus(String roomId, String userUuid, boolean isReady) {
        roomBroadcaster.broadcastToRoom(roomId, userUuid, isReady ? EventType.READY : EventType.UNREADY, userUuid);
    }


    private void broadcastExit(String roomId, String userUuid) {
        roomBroadcaster.broadcastToRoom(roomId, userUuid, EventType.EXIT, userUuid);
    }

    public void passRoomOwner(String roomId, RoomUserInfo newOwner) {
        roomRepository.updateAllFields(roomId, Map.of(
                RoomField.OWNER_UUID.getRedisField(), newOwner.getUserUuid(),
                RoomField.OWNER.getRedisField(), newOwner.getNickname()
        ));

        RoomMetadata updatedMetadata = RoomMetadata.fromRedisMap(roomId, roomRepository.getRoomData(roomId));
        roomBroadcaster.broadcastToRoom(roomId, newOwner.getUserUuid(), EventType.UPDATE, updatedMetadata);

        int currentUserCount = roomUserRepository.findUsersByRoomId(roomId).size();
        RoomSummaryRes summary = RoomSummaryRes.of(updatedMetadata, currentUserCount);
        roomBroadcaster.broadcastToRoomList(newOwner.getUserUuid(), EventType.UPDATE, summary);

        log.info("방장 권한이 {}에게 위임되었습니다. (roomId: {})", newOwner.getUserUuid(), roomId);
    }
}

