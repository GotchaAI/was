package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.AISaysRes;
import socket_server.domain.game.dto.GameRes;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.model.RoomEventType;

import java.time.LocalDateTime;

import static socket_server.common.constants.WebSocketConstants.GAME_PREFIX;
import static socket_server.common.constants.WebSocketConstants.ROOM_PREFIX;

@Service
@RequiredArgsConstructor
public class GameBroadCaster {
    private final JsonSerializer jsonSerializer;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    public void broadcastStartEvent(String userUuid, String roomId, AISaysRes aiSaysRes) {

        EventRes eventRes = new EventRes(
                RoomEventType.START,
                aiSaysRes,
                LocalDateTime.now()
        );

        RedisMessage redisMessage = new RedisMessage(
                userUuid,
                ROOM_PREFIX + roomId,
                jsonSerializer.serialize(eventRes,ErrorType.GAME)
        );

        objectRedisTemplate.convertAndSend(ROOM_PREFIX + roomId, jsonSerializer.serialize(redisMessage, ErrorType.GAME));
    }

    public void broadcastGameEvent(String senderUuid, String roomId, GameEventType gameEventType, Object data, String aiSays, LocalDateTime endTime) {
        GameRes gameRes = GameRes.builder()
                .eventType(gameEventType)
                .data(data)
                .aiSays(aiSays)
                .eventAt(LocalDateTime.now())
                .endTime(endTime)
                .build();

        objectRedisTemplate.convertAndSend(
                GAME_PREFIX + roomId,
                new RedisMessage(
                        senderUuid,
                        GAME_PREFIX + roomId,
                        jsonSerializer.serialize(gameRes, ErrorType.GAME)
                )
        );

    }
}
