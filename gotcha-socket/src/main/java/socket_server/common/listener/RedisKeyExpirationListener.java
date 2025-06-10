package socket_server.common.listener;

import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import socket_server.domain.room.service.RoomUserService;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final RoomUserService roomUserService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, RoomUserService roomUserService) {
        super(listenerContainer);
        this.roomUserService = roomUserService;
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        String expiredKey = message.toString();

        if (expiredKey.startsWith("disconnect:room:")) {
            String userUuid = expiredKey.replace("disconnect:room:", "");
            String roomId = roomUserService.findRoomIdByUserUuid(userUuid);

            if (roomId != null) {
                roomUserService.exitRoom(roomId, userUuid);
            }
        }
    }
}
