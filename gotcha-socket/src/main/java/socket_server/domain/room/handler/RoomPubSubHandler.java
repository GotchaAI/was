package socket_server.domain.room.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;

import java.util.List;

import static socket_server.common.constants.WebSocketConstants.ROOM_CREATE_INFO;
import static socket_server.common.constants.WebSocketConstants.ROOM_JOIN;

@Slf4j
@Service
@Qualifier("roomPubSubHandler")
public class RoomPubSubHandler extends PubSubHandler {


    public RoomPubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(ROOM_CREATE_INFO, this::roomCreateInfo);
        handlers.put(ROOM_JOIN, this::roomJoin);
    }

    private void roomCreateInfo(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        RoomMetadata roomMetadata = jsonSerializer.deserialize(redisMessage.payload(), RoomMetadata.class);
        messagingTemplate.convertAndSend(channel, roomMetadata);
    }

    private void roomJoin(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        // payload : List<RoomUserInfo>
        List<RoomUserInfo> roomUserInfoList = jsonSerializer.deserializeList(redisMessage.payload(), RoomUserInfo.class);
        messagingTemplate.convertAndSend(channel, roomUserInfoList);
    }

}
