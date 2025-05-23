package socket_server.domain.room.service;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.repository.RoomRepository;

import java.util.Map;

import static socket_server.common.constants.WebSocketConstants.ROOM_CREATE_INFO;

@Service
@Slf4j
public class RoomService {

    private final RoomIdService roomIdService;
    private final RoomUserService roomUserService;
    private final JsonSerializer jsonSerializer;
    private final RoomRepository roomRepository;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    public RoomService(
            RoomIdService roomIdService,
            RoomUserService roomUserService,
            RoomRepository roomRepository,
            RedisTemplate<String, Object> objectRedisTemplate,
            JsonSerializer jsonSerializer
    ) {
        this.roomIdService = roomIdService;
        this.roomUserService = roomUserService;
        this.roomRepository = roomRepository;
        this.objectRedisTemplate = objectRedisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    public void handleCreateRoom(CreateRoomRequest request, SecurityUserDetails userDetails) {
        roomUserService.checkUserNotInAnyRoom(userDetails.getUuid());
        RoomMetadata roomMetadata = createRoom(request, userDetails.getUuid());
        broadcastRoomInfo(userDetails.getUuid(), roomMetadata);
    }

    //todo : lua 스크립트 적용
    public RoomMetadata createRoom(CreateRoomRequest request, String ownerId) {

        String roomId = roomIdService.allocateRoomId();

        Map<String, String> roomData = Map.of (
                RoomField.TITLE.getRedisField(), request.title(),
                RoomField.OWNER.getRedisField(), ownerId,
                RoomField.HAS_PASSWORD.getRedisField(), String.valueOf(request.hasPassword()),
                RoomField.PASSWORD.getRedisField(), request.password(),
                RoomField.MAX.getRedisField(), String.valueOf(request.gameMode().getMaxPlayers()),
                RoomField.MIN.getRedisField(), String.valueOf(request.gameMode().getMinPlayers()),
                RoomField.AI_LEVEL.getRedisField(), request.aimode().name(),
                RoomField.GAME_MODE.getRedisField(), request.gameMode().name()
        );

        roomRepository.saveRoomData(roomId, roomData);

        log.info("User {} created Room {}", ownerId, roomId);
        return getRoomInfo(roomId);
    }

    public void broadcastRoomInfo(String userId, RoomMetadata metadata) {
        objectRedisTemplate.convertAndSend(ROOM_CREATE_INFO,
                new RedisMessage(userId, ROOM_CREATE_INFO, jsonSerializer.serialize(metadata))); //방 목록 생성 브로드 캐스트 용
    }

    public RoomMetadata getRoomInfo(String roomId) {
        Map<Object, Object> fields = roomRepository.getRoomData(roomId);
        return RoomMetadata.fromRedisMap(roomId, fields);
    }


}
