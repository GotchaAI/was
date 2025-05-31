package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomUserService;

@Component
public class ReadyRoomHandler implements RoomEventHandler{
    private final RoomUserService roomUserService;

    public ReadyRoomHandler(RoomUserService roomUserService) {
        this.roomUserService = roomUserService;
    }

    @Override
    public RoomEventType getEventType() {
        return RoomEventType.READY;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomUserService.updatePlayerReady(roomId, userDetails.getUuid(), true);
    }
}
