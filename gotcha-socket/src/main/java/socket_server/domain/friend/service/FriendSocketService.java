package socket_server.domain.friend.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.friend.dto.FriendEventRes;
import socket_server.domain.friend.dto.FriendEventType;
import socket_server.domain.friend.dto.FriendSummaryRes;

import java.time.LocalDateTime;

import static socket_server.common.constants.WebSocketConstants.FRIEND_PREFIX;

@Service
public class FriendSocketService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;
    private final ErrorType FRIEND_ERROR = ErrorType.FRIEND;

    public FriendSocketService(
            @Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
            JsonSerializer jsonSerializer
    ){
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    public void sendFriendAlert(String fromUserUuid, String toUserUuid, FriendSummaryRes friendSummaryRes, FriendEventType eventType) {
        FriendEventRes eventRes = new FriendEventRes(eventType, friendSummaryRes, LocalDateTime.now());

        RedisMessage redisMessage = new RedisMessage(
                fromUserUuid,
                FRIEND_PREFIX + toUserUuid,
                jsonSerializer.serialize(eventRes, FRIEND_ERROR)
        );

        redisTemplate.convertAndSend(FRIEND_PREFIX + toUserUuid, jsonSerializer.serialize(redisMessage, FRIEND_ERROR));
    }
}
