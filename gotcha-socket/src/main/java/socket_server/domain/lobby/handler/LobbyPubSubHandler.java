package socket_server.domain.lobby.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.dto.EventRes;

import static socket_server.common.constants.WebSocketConstants.*;

@Slf4j
@Service
@Qualifier("lobbyPubSubHandler")
public class LobbyPubSubHandler  extends PubSubHandler {

    public LobbyPubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(LOBBY_JOIN_CHANNEL, this:: handleRedisResMessage);
        handlers.put(LOBBY_ROOM_CREATE_CHANNEL, this::handleRedisResMessage);
        handlers.put(LOBBY_ROOM_LIST_EVENT, this::handleEventResMessage);
    }

    private void handleEventResMessage(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        EventRes eventRes = jsonSerializer.deserialize(redisMessage.payload(), EventRes.class, ErrorType.LOBBY);
        messagingTemplate.convertAndSend(channel, eventRes);
    }

    private void handleRedisResMessage(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        messagingTemplate.convertAndSend(channel, redisMessage);
    }


}
