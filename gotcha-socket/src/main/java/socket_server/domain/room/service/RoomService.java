package socket_server.domain.room.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.domain.room.RoomField.RoomField;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.model.RoomMetadata;

import java.util.Map;

@Service
public class RoomService {

    private final RoomIdService roomIdService;
    private final RedisTemplate<String, String> redisTemplate;

    public RoomService(
            RoomIdService roomIdService,
            @Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate
    ) {
        this.roomIdService = roomIdService;
        this.redisTemplate = redisTemplate;
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

        redisTemplate.opsForHash().putAll(getRoomKey(roomId), roomData);
        return getRoomInfo(roomId);
    }

    public RoomMetadata getRoomInfo(String roomId) {
        Map<Object, Object> fields = redisTemplate.opsForHash().entries(getRoomKey(roomId));
        return RoomMetadata.fromRedisMap(roomId, fields);
    }

    private String getRoomKey(String roomId) {
        return "room:"  + roomId;
    }
}
