package socket_server.domain.friend.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.friend.dto.FriendEventRes;

import static socket_server.common.constants.WebSocketConstants.FRIEND_PREFIX;

@Slf4j
@Service
@Qualifier("friendPubSubHandler")
public class FriendPubSubHandler   extends PubSubHandler {
    public FriendPubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(FRIEND_PREFIX, this:: handleEventResMessage);
    }

    private void handleEventResMessage(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        FriendEventRes eventRes = jsonSerializer.deserialize(redisMessage.payload(), FriendEventRes.class, ErrorType.FRIEND);
        messagingTemplate.convertAndSend(channel, eventRes);
    }
}

