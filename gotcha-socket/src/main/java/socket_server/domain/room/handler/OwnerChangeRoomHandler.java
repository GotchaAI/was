package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomUserService;

@Component
public class OwnerChangeRoomHandler implements RoomEventHandler{
    private final RoomUserService roomUserService;

    public OwnerChangeRoomHandler(RoomUserService roomUserService) {
        this.roomUserService = roomUserService;
    }
    @Override
    public EventType getEventType() {
        return EventType.OWNER_CHANGE;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomUserService.passRoomOwner(roomId, userDetails.getUuid(), request.content());
    }
}
