package socket_server.domain.room.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.dto.EventRes;

import static socket_server.common.constants.WebSocketConstants.ROOM_LIST_EVENT;
import static socket_server.common.constants.WebSocketConstants.ROOM_EVENT;
import static socket_server.common.constants.WebSocketConstants.ROOM_OWNER_CREATE_INFO;

@Slf4j
@Service
@Qualifier("roomPubSubHandler")
public class RoomPubSubHandler extends PubSubHandler {


    public RoomPubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(ROOM_LIST_EVENT, this::handleEventResMessage);
        handlers.put(ROOM_OWNER_CREATE_INFO, this::handleEventResMessage);
        handlers.put(ROOM_EVENT, this::handleEventResMessage);
    }

    private void handleEventResMessage(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        EventRes eventRes = jsonSerializer.deserialize(redisMessage.payload(), EventRes.class);
        messagingTemplate.convertAndSend(channel, eventRes);
    }

}
