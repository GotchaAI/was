package socket_server.domain.room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.model.RoomEventType;

import java.time.LocalDateTime;

import static socket_server.common.constants.WebSocketConstants.ROOM_PREFIX;

@Component
@RequiredArgsConstructor
public class RoomBroadcaster {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonSerializer jsonSerializer;

    public void broadcastToRoom(String roomId, String senderId, RoomEventType type, Object data) {
        EventRes event = new EventRes(type, data, LocalDateTime.now());
        RedisMessage message = new RedisMessage(senderId, ROOM_PREFIX + roomId, jsonSerializer.serialize(event, ErrorType.ROOM));
        redisTemplate.convertAndSend(ROOM_PREFIX + roomId, jsonSerializer.serialize(message, ErrorType.ROOM));
    }

    public void sendToUser(String topic, String userUuid, Object payload) {
        RedisMessage message = new RedisMessage(userUuid, topic, jsonSerializer.serialize(payload, ErrorType.ROOM));
        redisTemplate.convertAndSend(topic, jsonSerializer.serialize(message, ErrorType.ROOM));
    }

}
