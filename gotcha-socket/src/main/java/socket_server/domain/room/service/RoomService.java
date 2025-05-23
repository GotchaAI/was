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
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.repository.RoomRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static socket_server.common.constants.WebSocketConstants.ROOM_CREATE_INFO;
import static socket_server.common.constants.WebSocketConstants.ROOM_EVENT;

@Service
@Slf4j
public class RoomService {

    private final RoomIdService roomIdService;
    private final RoomUserService roomUserService;
    private final JsonSerializer jsonSerializer;
    private final RoomRepository roomRepository;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    private final RedisTemplate<String, String> redisTemplate;

    public RoomService(
            RoomIdService roomIdService,
            RoomUserService roomUserService,
            RoomRepository roomRepository,
            RedisTemplate<String, Object> objectRedisTemplate,
            @Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
            JsonSerializer jsonSerializer
    ) {
        this.roomIdService = roomIdService;
        this.roomUserService = roomUserService;
        this.roomRepository = roomRepository;
        this.objectRedisTemplate = objectRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    public void handleCreateRoom(CreateRoomRequest request, SecurityUserDetails userDetails) {
        roomUserService.checkUserNotInAnyRoom(userDetails.getUuid());
        RoomMetadata roomMetadata = createRoom(request, userDetails);
        broadcastRoomInfo(userDetails.getUuid(), roomMetadata);
    }

    //todo : lua 스크립트 적용
    public RoomMetadata createRoom(CreateRoomRequest request, SecurityUserDetails userDetails) {

        String roomId = roomIdService.allocateRoomId();

        Map<String, String> roomData = new HashMap<>();
        roomData.put(RoomField.TITLE.getRedisField(), request.title());
        roomData.put(RoomField.OWNER.getRedisField(), userDetails.getNickname());
        roomData.put(RoomField.HAS_PASSWORD.getRedisField(), String.valueOf(request.hasPassword()));
        if (request.hasPassword()) {
            if (request.password() == null || request.password().isBlank()) {
                throw new CustomException(RoomExceptionCode.PASSWORD_REQUIRED_BUT_MISSING);
            }
            roomData.put(RoomField.PASSWORD.getRedisField(), request.password());
        }
        roomData.put(RoomField.MAX.getRedisField(), String.valueOf(request.gameMode().getMaxPlayers()));
        roomData.put(RoomField.MIN.getRedisField(), String.valueOf(request.gameMode().getMinPlayers()));
        roomData.put(RoomField.AI_LEVEL.getRedisField(), request.aimode().name());
        roomData.put(RoomField.GAME_MODE.getRedisField(), request.gameMode().name());
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

    public void broadcastRoomInfo(String userUuid, RoomMetadata metadata) {
        objectRedisTemplate.convertAndSend(ROOM_CREATE_INFO,
                new RedisMessage(userUuid, ROOM_CREATE_INFO, jsonSerializer.serialize(metadata))); //방 목록 생성 브로드 캐스트 용
    }

    public RoomMetadata getRoomInfo(String roomId) {
        Map<Object, Object> fields = roomRepository.getRoomData(roomId);
        return RoomMetadata.fromRedisMap(roomId, fields);
    }


}
