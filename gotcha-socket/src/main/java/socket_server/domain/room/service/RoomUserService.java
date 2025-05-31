package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.lobby.dto.RoomIdRes;
import socket_server.domain.lobby.service.LobbyBroadCaster;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomSummaryRes;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomRepository;
import socket_server.domain.room.repository.RoomUserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static socket_server.common.exception.room.RoomExceptionCode.CANNOT_KICK_SELF;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomUserService {
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;
    private final RoomBroadcaster roomBroadcaster;
    private final LobbyBroadCaster lobbyBroadCaster;
    private final RoomIdService roomIdService;
    private final ErrorType ROOM_ERROR = ErrorType.ROOM;

    public void joinRoom(String roomId, String userUuid, String nickname, ErrorType errorType) {
        RoomUserInfo roomUserInfo = RoomUserInfo.builder().
                userUuid(userUuid).
                nickname(nickname).
                ready(false).
                build();

        roomUserRepository.saveUserToRoom(roomUserInfo, roomId, errorType);
        log.info("User {} joined room {}", userUuid, roomId);
    }

    public void updatePlayerReady(String roomId, String userUuid, boolean isReady) {
        RoomUserInfo userInfo = roomUserRepository.findUserInfoInRoom(roomId, userUuid, ROOM_ERROR);

        if (userInfo == null) {
            throw new SocketCustomException(ErrorType.ROOM, RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        userInfo.setReady(isReady);
        roomUserRepository.saveUserToRoom(userInfo, roomId, ROOM_ERROR);
        broadcastReadyStatus(roomId, userUuid, isReady);
    }

    public void exitRoom(String roomId, String userUuid) {
        processUserExit(roomId, userUuid, false);
        broadcastExit(roomId, userUuid);
    }

    public String findRoomIdByUserUuid(String userUuid) {
        return roomUserRepository.findRoomIdByUserUuid(userUuid);
    }

    public void checkGameStart(String roomId, GameType gameType) {
        List<RoomUserInfo> users = roomUserRepository.findUsersByRoomId(roomId, ErrorType.ROOM);
        for(RoomUserInfo user : users) {
            if(!user.isReady()) {
                throw new SocketCustomException(ErrorType.ROOM, RoomExceptionCode.NOT_ALL_PLAYER_READY);
            }
        }

        if(gameType.equals(GameType.TRICK_MYOMYO)) {
            if(users.size() != 2) {
                throw new SocketCustomException(ErrorType.ROOM, RoomExceptionCode.INVALID_GAME_PLAYERS);
            }
        }
        else {
            if(users.size() != 1) {
                throw new SocketCustomException(ErrorType.ROOM, RoomExceptionCode.INVALID_GAME_PLAYERS);
            }
        }
    }

    public void kickPlayer(String roomId, String userUuid, String kickPlayerUuid) {
        if (!validateRoomOwner(roomId, userUuid)) {
            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.NOT_ROOM_OWNER);
        }
        if (userUuid.equals(kickPlayerUuid)) {
            throw new SocketCustomException (ROOM_ERROR, CANNOT_KICK_SELF);
        }
        processUserExit(roomId, kickPlayerUuid, true);
    }

    public void passRoomOwner(String roomId, String oldOwnerId, String newOwnerId) {
        if (!validateRoomOwner(roomId, oldOwnerId)) {
            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.NOT_ROOM_OWNER);
        }

        RoomUserInfo newOwner = roomUserRepository.findUserInfoInRoom(roomId, newOwnerId, ROOM_ERROR);

        changeRoomOwner(roomId, newOwner);
    }

    private void processUserExit(String roomId, String userUuid, boolean isKicked) {
        roomBroadcaster.broadcastToRoom(roomId, "SYSTEM", isKicked ? RoomEventType.KICK : RoomEventType.EXIT, userUuid);

        boolean isOwner = validateRoomOwner(roomId, userUuid);
        roomUserRepository.removeUserFromRoom(roomId, userUuid);
        List<RoomUserInfo> remainingUsers = roomUserRepository.findUsersByRoomId(roomId, ROOM_ERROR);
        RoomMetadata roomMetadata = RoomMetadata.fromRedisMap(roomId, roomRepository.getRoomData(roomId));

        RoomSummaryRes roomSummaryRes = RoomSummaryRes.of(roomMetadata, remainingUsers.size());
        lobbyBroadCaster.broadcastToRoomList("SYSTEM", RoomEventType.UPDATE, roomSummaryRes);

        if (isOwner) {
            if (!remainingUsers.isEmpty()) {
                RoomUserInfo newOwner = remainingUsers.get(0);
                changeRoomOwner(roomId, newOwner);
            } else {
                log.info("방 {}에 유저가 없어 방을 삭제합니다.", roomId);
                roomRepository.deleteRoom(roomId);
                roomUserRepository.deleteUserList(roomId);
                roomBroadcaster.broadcastToRoom(roomId, "SYSTEM", RoomEventType.DELETE, "방이 삭제되었습니다");
                lobbyBroadCaster.broadcastToRoomList("SYSTEM", RoomEventType.DELETE, new RoomIdRes(roomMetadata.getId()));
                roomIdService.releaseRoomId(roomId, ROOM_ERROR);
            }
        }
    }

    public void changeRoomOwner(String roomId, RoomUserInfo newOwner) {
        roomRepository.updateAllFields(roomId, Map.of(
                RoomField.OWNER_UUID.getRedisField(), newOwner.getUserUuid(),
                RoomField.OWNER.getRedisField(), newOwner.getNickname()
        ));

        RoomMetadata updatedMetadata = RoomMetadata.fromRedisMap(roomId, roomRepository.getRoomData(roomId));
        roomBroadcaster.broadcastToRoom(roomId, newOwner.getUserUuid(), RoomEventType.UPDATE, updatedMetadata);

        int currentUserCount = roomUserRepository.findUsersByRoomId(roomId, ROOM_ERROR).size();
        RoomSummaryRes roomSummaryRes = RoomSummaryRes.of(updatedMetadata, currentUserCount);
        lobbyBroadCaster.broadcastToRoomList("SYSTEM", RoomEventType.UPDATE, roomSummaryRes);
        log.info("방장 권한이 {}에게 위임되었습니다. (roomId: {})", newOwner.getUserUuid(), roomId);
    }

    public RoomMetadata validateRoomOwnerAndGetRoomMetadata(String roomId, String userUuid) {
        if(!validateRoomOwner(roomId, userUuid)){
            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.NOT_ROOM_OWNER);
        }

        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
        return RoomMetadata.fromRedisMap(roomId, roomData);
    }

    private boolean validateRoomOwner(String roomId, String userUuid) {
        //방이 실존하는지 확인
        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
        if (roomData == null || roomData.isEmpty()) {
            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.INVALID_ROOM_ID);
        }

        // 유저가 방에 실제 존재하는지 확인
        RoomUserInfo userInfo = roomUserRepository.findUserInfoInRoom(roomId, userUuid, ROOM_ERROR);
        if (userInfo == null) {
            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        // 방장이 맞는지 확인
        String ownerUuid = (String) roomData.get(RoomField.OWNER_UUID.getRedisField());
        return userUuid.equals(ownerUuid);
    }

    public void broadcastUserInRoomInfo(String roomId, String userId, ErrorType errorType){
        List<RoomUserInfo> userList = roomUserRepository.findUsersByRoomId(roomId, errorType);
        roomBroadcaster.broadcastToRoom(roomId, userId, RoomEventType.JOIN, userList);
    }

    private void broadcastReadyStatus(String roomId, String userUuid, boolean isReady) {
        roomBroadcaster.broadcastToRoom(roomId, userUuid, isReady ? RoomEventType.READY : RoomEventType.UNREADY, userUuid);
    }

    private void broadcastExit(String roomId, String userUuid) {
        roomBroadcaster.broadcastToRoom(roomId, userUuid, RoomEventType.EXIT, userUuid);
    }

    public void validateUserCanJoinRoom(String roomId, String userUuid, String password, ErrorType errorType) {
        // 유저가 다른 방에 이미 속해있는지 확인
        checkUserNotInAnyRoom(userUuid, errorType);
        // 방 존재 여부 및 비밀번호 검증
        validateRoomExistsAndPassword(roomId, password, errorType);
    }

    public void checkUserNotInAnyRoom(String userUuid, ErrorType errorType) {
        String value = roomUserRepository.findRoomIdByUserUuid(userUuid);
        log.info("⭐⭐당신이 속한 대기방은 이겁니다 : "+value);
        if (value != null) {
            throw new SocketCustomException(errorType, RoomExceptionCode.USER_ALREADY_IN_ANOTHER_ROOM);
        }
    }

    private void validateRoomExistsAndPassword(String roomId, String password, ErrorType errorType) {
        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
        if (roomData == null || roomData.isEmpty()) {
            throw new SocketCustomException(errorType, RoomExceptionCode.INVALID_ROOM_ID);
        }

        boolean hasPassword = Boolean.parseBoolean(
                Optional.ofNullable(roomData.get(RoomField.HAS_PASSWORD.getRedisField()))
                        .map(Object::toString)
                        .orElse("false")
        );

        if (hasPassword) {
            String expectedPassword = (String) roomData.get(RoomField.PASSWORD.getRedisField());
            if (expectedPassword == null || !expectedPassword.equals(password)) {
                throw new SocketCustomException(errorType, RoomExceptionCode.INCORRECT_PASSWORD);
            }
        }

    }

}

