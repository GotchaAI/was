package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomUserService;

@Component
public class ExitRoomHandler implements RoomEventHandler {
    private final RoomUserService roomUserService;

    public ExitRoomHandler(RoomUserService roomUserService) {
        this.roomUserService = roomUserService;
    }

    @Override
    public RoomEventType getEventType() {
        return RoomEventType.EXIT;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomUserService.exitRoom(roomId, userDetails.getUuid());
    }
}
