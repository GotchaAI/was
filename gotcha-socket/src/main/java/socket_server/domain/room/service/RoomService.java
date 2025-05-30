package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.chat.dto.ChatMessage;
import socket_server.domain.chat.dto.ChatType;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomJoinRes;
import socket_server.domain.room.dto.RoomSummaryRes;
import socket_server.domain.room.dto.RoomUpdateReq;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomRepository;
import socket_server.domain.room.repository.RoomUserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static socket_server.common.constants.WebSocketConstants.ROOM_EVENT;
import static socket_server.common.constants.WebSocketConstants.ROOM_OWNER_CREATE_INFO;

@Service
@Slf4j
public class RoomService {
    private final RoomUserService roomUserService;
    private final JsonSerializer jsonSerializer;
    private final RoomRepository roomRepository;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RoomIdService roomIdService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RoomUserRepository roomUserRepository;
    private final RoomBroadcaster roomBroadcaster;

    public RoomService(
            RoomIdService roomIdService,
            RoomUserService roomUserService,
            RoomRepository roomRepository,
            RedisTemplate<String, Object> objectRedisTemplate,
            @Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
            JsonSerializer jsonSerializer,
            RoomUserRepository roomUserRepository,
            RoomBroadcaster roomBroadcaster) {
        this.roomIdService = roomIdService;
        this.roomUserService = roomUserService;
        this.roomRepository = roomRepository;
        this.objectRedisTemplate = objectRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
        this.roomUserRepository = roomUserRepository;
        this.roomBroadcaster = roomBroadcaster;
    }

    public void handleCreateRoom(CreateRoomRequest request, SecurityUserDetails userDetails) {
        roomUserService.checkUserNotInAnyRoom(userDetails.getUuid());
        RoomMetadata roomMetadata = createRoom(request, userDetails);
        roomUserService.joinRoom(
                roomMetadata.getId(),
                userDetails.getUuid(),
                userDetails.getNickname(),
                request.hasPassword() ? request.password() : null
        );
        sendRoomMetadataToOwner(roomMetadata, userDetails.getUuid());
        broadcastCreatedRoomInfo(userDetails.getUuid(), roomMetadata);
    }

    //todo : lua 스크립트 적용
    public RoomMetadata createRoom(CreateRoomRequest request, SecurityUserDetails userDetails) {

        String roomId = roomIdService.allocateRoomId();

        if (request.hasPassword() && (request.password() == null || request.password().isBlank())) {
            throw new CustomException(RoomExceptionCode.PASSWORD_REQUIRED_BUT_MISSING);
        }

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
            throw new CustomException(RoomExceptionCode.USER_NOT_IN_ROOM);
        }

        ChatMessage chatMessage = new ChatMessage(
                userDetails.getNickname(),
                content,
                ChatType.ROOM,
                LocalDateTime.now()
        );

        EventRes eventRes = new EventRes(
                EventType.CHAT,
                chatMessage,
                chatMessage.sentAt()
        );

        RedisMessage redisMessage = new RedisMessage(
                userDetails.getUuid(),
                ROOM_EVENT + roomId,
                jsonSerializer.serialize(eventRes)
        );

        redisTemplate.convertAndSend(ROOM_EVENT + roomId, jsonSerializer.serialize(redisMessage));

        log.info("chat - roomId: {}, user: {}, content: {}", roomId, userDetails.getUuid(), content);
    }

    public void updateRoomField(String roomId, RoomUpdateReq roomUpdateReq, String userUuid) {
        RoomMetadata roomMetadata = roomUserService.validateRoomOwnerAndGetRoomMetadata(roomId, userUuid);

        if (roomUpdateReq.hasPassword() && (roomUpdateReq.password() == null || roomUpdateReq.password().isBlank())) {
            throw new CustomException(RoomExceptionCode.PASSWORD_REQUIRED_BUT_MISSING);
        }

//        //이전 방 내용 조회 -> 로직 확인용
//        Map<Object, Object> exitRoom = roomRepository.getRoomData(roomId);
//        log.info("✅ 이전 방 필드 정보: {}", exitRoom);

        roomMetadata.setTitle(roomUpdateReq.title());
        roomMetadata.setHasPassword(roomUpdateReq.hasPassword());
        roomMetadata.setPassword(roomUpdateReq.hasPassword() ? roomUpdateReq.password() : "");
        roomMetadata.setDifficulty(roomUpdateReq.difficulty());
        roomMetadata.setRoundCount(roomUpdateReq.roundCount());

        roomRepository.updateAllFields(roomId, roomMetadata.toRedisMap());

//        //제대로 바뀐게 맞나 조회 -> 로직 확인용
//        Map<Object, Object> updatedRoom = roomRepository.getRoomData(roomId);
//        log.info("✅ 수정된 방 필드 정보: {}", updatedRoom);
        broadcastUpdatedRoomInfoToListAndRoom(roomId, roomMetadata);
    }

    private void broadcastUpdatedRoomInfoToListAndRoom(String roomId, RoomMetadata metadata) {
        int currentUser = roomUserRepository.findUsersByRoomId(roomId).size();

        RoomSummaryRes summary = RoomSummaryRes.of(metadata, currentUser);
        roomBroadcaster.broadcastToRoomList("SYSTEM", EventType.UPDATE, summary);

        List<RoomUserInfo> userList = roomUserRepository.findUsersByRoomId(roomId);
        RoomJoinRes roomJoinRes = new RoomJoinRes(metadata, userList);
        roomBroadcaster.broadcastToRoom(roomId, "SYSTEM", EventType.UPDATE, roomJoinRes);

        log.info("방 {} 업데이트 정보를 ROOM_LIST_EVENT 및 ROOM_EVENT 로 브로드캐스트 완료", roomId);
    }

    public void broadcastCreatedRoomInfo(String userUuid, RoomMetadata metadata) {
        int currentUser = roomUserRepository.findUsersByRoomId(metadata.getId()).size();
        RoomSummaryRes summary = RoomSummaryRes.of(metadata, currentUser);

        roomBroadcaster.broadcastToRoomList(userUuid, EventType.CREATE, summary);
    }

    private void sendRoomMetadataToOwner(RoomMetadata metadata, String userUuid) {
        EventRes eventRes = new EventRes(
                EventType.JOIN,
                metadata,
                LocalDateTime.now()
        );

        RedisMessage message = new RedisMessage(userUuid, ROOM_OWNER_CREATE_INFO + userUuid, jsonSerializer.serialize(eventRes));
        objectRedisTemplate.convertAndSend(ROOM_OWNER_CREATE_INFO + userUuid, jsonSerializer.serialize(message));

        log.info("방장 {} 에게 방 전체 정보 전송 : {}", ROOM_OWNER_CREATE_INFO + userUuid, metadata.getId());
    }


    public RoomMetadata getRoomInfo(String roomId) {
        Map<Object, Object> fields = roomRepository.getRoomData(roomId);
        return RoomMetadata.fromRedisMap(roomId, fields);
    }
}
