package socket_server.domain.lobby.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.model.RoomEventType;

import java.time.LocalDateTime;

import static socket_server.common.constants.WebSocketConstants.LOBBY_ROOM_LIST_EVENT;

@Component
@RequiredArgsConstructor
public class LobbyBroadCaster {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonSerializer jsonSerializer;

    public void broadcastToRoomList(String senderId, RoomEventType type, Object data) {
        EventRes event = new EventRes(type, data, LocalDateTime.now());
        RedisMessage message = new RedisMessage(senderId, LOBBY_ROOM_LIST_EVENT, jsonSerializer.serialize(event, ErrorType.LOBBY));
        redisTemplate.convertAndSend(LOBBY_ROOM_LIST_EVENT, jsonSerializer.serialize(message, ErrorType.LOBBY));
    }

    public void sendToUser(String topic, String userUuid, Object payload) {
        RedisMessage message = new RedisMessage(userUuid, topic, jsonSerializer.serialize(payload, ErrorType.LOBBY));
        redisTemplate.convertAndSend(topic, jsonSerializer.serialize(message, ErrorType.LOBBY));
    }
}

