package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.chat.ChatMessage;
import gotcha_domain.chat.ChatType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.chat.service.ChatLogService;
import socket_server.domain.lobby.dto.CreateRoomReq;
import socket_server.domain.lobby.dto.RoomDetailRes;
import socket_server.domain.lobby.service.LobbyBroadCaster;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.dto.RoomInfoRes;
import socket_server.domain.room.dto.RoomListReq;
import socket_server.domain.room.dto.RoomSummaryRes;
import socket_server.domain.room.dto.RoomUpdateReq;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomRepository;
import socket_server.domain.room.repository.RoomUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static socket_server.common.constants.WebSocketConstants.ROOM_PREFIX;

@Service
@Slf4j
public class RoomService {
    private final RoomUserService roomUserService;
    private final JsonSerializer jsonSerializer;
    private final RoomRepository roomRepository;
    private final RoomIdService roomIdService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RoomUserRepository roomUserRepository;
    private final RoomBroadcaster roomBroadcaster;
    private final LobbyBroadCaster lobbyBroadCaster;
    private final ChatLogService chatLogService;
    private final ErrorType ROOM_ERROR = ErrorType.ROOM;

    public RoomService(
            RoomIdService roomIdService,
            RoomUserService roomUserService,
            RoomRepository roomRepository,
            @Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
            JsonSerializer jsonSerializer,
            RoomUserRepository roomUserRepository,
            RoomBroadcaster roomBroadcaster,
            LobbyBroadCaster lobbyBroadCaster,
            ChatLogService chatLogService) {
        this.roomIdService = roomIdService;
        this.roomUserService = roomUserService;
        this.roomRepository = roomRepository;
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
        this.roomUserRepository = roomUserRepository;
        this.roomBroadcaster = roomBroadcaster;
        this.lobbyBroadCaster = lobbyBroadCaster;
        this.chatLogService = chatLogService;
    }

    //todo : lua 스크립트 적용
    public RoomMetadata createRoom(CreateRoomReq request, SecurityUserDetails userDetails) {

        String roomId = roomIdService.allocateRoomId(ROOM_ERROR);
        Map<String, String> roomData = new HashMap<>();
        roomData.put(RoomField.TITLE.getRedisField(), request.title());
        roomData.put(RoomField.OWNER.getRedisField(), userDetails.getNickname());
        roomData.put(RoomField.HAS_PASSWORD.getRedisField(), String.valueOf(request.hasPassword()));
        roomData.put(RoomField.PASSWORD.getRedisField(), request.password());
        roomData.put(RoomField.ROUND_COUNT.getRedisField(), String.valueOf(request.roundCount()));
        roomData.put(RoomField.MAX.getRedisField(), String.valueOf(request.gameType().getMaxPlayers()));
        roomData.put(RoomField.MIN.getRedisField(), String.valueOf(request.gameType().getMinPlayers()));
        roomData.put(RoomField.DIFFICULTY.getRedisField(), request.difficulty().name());
        roomData.put(RoomField.GAME_TYPE.getRedisField(), request.gameType().name());
        roomData.put(RoomField.OWNER_UUID.getRedisField(), userDetails.getUuid());

        roomRepository.saveRoomData(roomId, roomData);

        log.info("User {} created Room {}", userDetails.getNickname(), roomId);
        return getRoomInfo(roomId);
    }

    public void sendRoomChat(String roomId, SecurityUserDetails userDetails, String content) {
        String userRoomId = roomUserService.findRoomIdByUserUuid(userDetails.getUuid());
        if (!roomId.equals(userRoomId)) {
            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        ChatMessage chatMessage = new ChatMessage(
                userDetails.getNickname(),
                content,
                ChatType.ROOM,
                LocalDateTime.now()
        );

        EventRes eventRes = new EventRes(
                RoomEventType.CHAT,
                chatMessage,
                chatMessage.sentAt()
        );

        RedisMessage redisMessage = new RedisMessage(
                userDetails.getUuid(),
                ROOM_PREFIX + roomId,
                jsonSerializer.serialize(eventRes, ROOM_ERROR)
        );

        redisTemplate.convertAndSend(ROOM_PREFIX + roomId, jsonSerializer.serialize(redisMessage, ROOM_ERROR));

        log.info("chat - roomId: {}, user: {}, content: {}", roomId, userDetails.getUuid(), content);

        chatLogService.saveChatMessage(ChatType.ROOM, roomId, chatMessage, userDetails.getUuid());
    }

    public void updateRoomField(String roomId, RoomUpdateReq roomUpdateReq, String userUuid) {
        RoomMetadata roomMetadata = roomUserService.validateRoomOwnerAndGetRoomMetadata(roomId, userUuid);

//        if (roomUpdateReq.hasPassword() && (roomUpdateReq.password() == null || roomUpdateReq.password().isBlank())) {
//            throw new SocketCustomException(ROOM_ERROR, RoomExceptionCode.PASSWORD_REQUIRED_BUT_MISSING);
//        }

        roomMetadata.setTitle(roomUpdateReq.title());
//        roomMetadata.setHasPassword(roomUpdateReq.hasPassword());
//        roomMetadata.setPassword(roomUpdateReq.hasPassword() ? roomUpdateReq.password() : "");
        roomMetadata.setDifficulty(roomUpdateReq.difficulty());
        roomMetadata.setRoundCount(roomUpdateReq.roundCount());

        roomRepository.updateAllFields(roomId, roomMetadata.toRedisMap());
        broadcastUpdatedRoomInfoToListAndRoom(roomId, roomMetadata);
    }

    public void validateRoomExistsAndPassword(String roomId, String password, ErrorType errorType) {
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

    private void broadcastUpdatedRoomInfoToListAndRoom(String roomId, RoomMetadata metadata) {
        int currentUser = roomUserRepository.findUsersByRoomId(roomId, ROOM_ERROR).size();
        RoomSummaryRes summary = RoomSummaryRes.of(metadata, currentUser);
        lobbyBroadCaster.broadcastToRoomList("SYSTEM", RoomEventType.UPDATE, summary);

        List<RoomUserInfo> userList = new ArrayList<>(roomUserRepository.findUsersByRoomId(roomId, ROOM_ERROR));
        sortUserListWithOwnerFirst(userList, metadata.getOwnerUuid());
        RoomDetailRes roomDetailRes = new RoomDetailRes(RoomInfoRes.from(metadata), userList);
        roomBroadcaster.broadcastToRoom(roomId, "SYSTEM", RoomEventType.UPDATE, roomDetailRes);

        log.info("방 {} 업데이트 정보를 ROOM_LIST_EVENT 및 ROOM_EVENT 로 브로드캐스트 완료", roomId);
    }

    public RoomMetadata getRoomInfo(String roomId) {
        Map<Object, Object> fields = roomRepository.getRoomData(roomId);
        return RoomMetadata.fromRedisMap(roomId, fields);
    }

    public List<RoomSummaryRes> getAllRoomSummaries(RoomListReq roomListReq) {
        List<RoomSummaryRes> summaries = new ArrayList<>();
        try (Cursor<String> roomKeys = roomRepository.scanRoomKeys()) {
            while (roomKeys.hasNext()) {
                String roomKey = roomKeys.next();
                String roomId = roomKey.replaceFirst("room:data:", "");

                Map<Object, Object> roomData = roomRepository.getRoomData(roomId);
                if (roomData == null || roomData.isEmpty()) {
                    continue;
                }

                RoomMetadata metadata = RoomMetadata.fromRedisMap(roomId, roomData);

                if (!metadata.getGameType().equals(roomListReq.gameType())) {
                    continue;
                }
                if (roomListReq.difficulty() != null && !roomListReq.difficulty().equals(metadata.getDifficulty())) {
                    continue;
                }

                int currentUser = roomUserRepository.findUsersByRoomId(roomId, ROOM_ERROR).size();
                summaries.add(RoomSummaryRes.of(metadata, currentUser));
            }
        }
        return summaries;
    }


    public RoomDetailRes getRoomDetails(String roomId, String userUuid) {
        Map<Object, Object> roomData = roomRepository.getRoomData(roomId);

        if (roomData == null || roomData.isEmpty()) {
            throw new CustomException(RoomExceptionCode.INVALID_ROOM_ID);
        }

        if (!roomUserService.validateUserInRoom(roomId, userUuid)) {
            throw new CustomException(RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        RoomMetadata metadata = RoomMetadata.fromRedisMap(roomId, roomData);
        List<RoomUserInfo> userList = new ArrayList<>(roomUserRepository.findUsersByRoomId(roomId, ROOM_ERROR));
        sortUserListWithOwnerFirst(userList, metadata.getOwnerUuid());

        return new RoomDetailRes(RoomInfoRes.from(metadata), userList);
    }

    private void sortUserListWithOwnerFirst(List<RoomUserInfo> userList, String ownerUuid) {
        userList.sort((u1, u2) -> {
            if (u1.getUserUuid().equals(ownerUuid)) {
                return -1;
            }
            if (u2.getUserUuid().equals(ownerUuid)) {
                return 1;
            }
            return 0;
        });
    }
}
