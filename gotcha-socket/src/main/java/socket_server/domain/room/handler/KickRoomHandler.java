package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.service.RoomUserService;

@Component
public class KickRoomHandler implements RoomEventHandler {
    private RoomUserService roomUserService;

    public KickRoomHandler(RoomUserService roomUserService) {
        this.roomUserService = roomUserService;
    }

    @Override
    public RoomEventType getEventType() {
        return RoomEventType.KICK;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomUserService.kickPlayer(roomId, userDetails.getUuid(), request.content());
    }
}
