package socket_server.domain.room.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.listener.PubSubHandler;
import socket_server.domain.room.model.RoomMetadata;
import static socket_server.common.constants.WebSocketConstants.ROOM_CREATE_INFO;

@Slf4j
@Service
@Qualifier("roomPubSubHandler")
public class RoomPubSubHandler extends PubSubHandler {
    private final RoomMetadata roomMetadata;

    public RoomPubSubHandler(SimpMessagingTemplate messagingTemplate, RoomMetadata roomMetadata) {
        super(messagingTemplate);
        this.roomMetadata = roomMetadata;
    }

    @Override
    protected void initHandlers() {
        handlers.put(ROOM_CREATE_INFO, this::roomCreateInfo);
    }

    private void roomCreateInfo(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        RoomMetadata roomMetadata = convertMessageToDto(redisMessage.payload(), RoomMetadata.class);
        messagingTemplate.convertAndSend(channel, roomMetadata);
    }


}
