package socket_server.domain.personal.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.model.RoomMetadata;

import static socket_server.common.constants.WebSocketConstants.PERSONAL_ROOM_CREATE_RESPONSE;

@Slf4j
@Service
@Qualifier("PersonalPubSubHandler")
public class PersonalPubSubHandler extends PubSubHandler {

    public PersonalPubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(PERSONAL_ROOM_CREATE_RESPONSE, this::roomCreateResponse);
    }

    private void roomCreateResponse (String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        RoomMetadata roomMetadata = jsonSerializer.deserialize((String) redisMessage.payload(), RoomMetadata.class);
        messagingTemplate.convertAndSend(channel, roomMetadata);
    }


}

