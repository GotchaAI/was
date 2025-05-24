package socket_server.domain.game.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.GameReadyStatus;
import socket_server.domain.room.dto.EventRes;

import static socket_server.common.constants.WebSocketConstants.*;

@Service
@Qualifier("gamePubSubHandler")
public class GamePubSubHandler extends PubSubHandler {

    public GamePubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(GAME_PREFIX, this::gameEvent);
    }

    private void gameEvent(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        EventRes eventRes = jsonSerializer.deserialize(redisMessage.payload(), EventRes.class);
        messagingTemplate.convertAndSend(channel, eventRes);
    }

}
