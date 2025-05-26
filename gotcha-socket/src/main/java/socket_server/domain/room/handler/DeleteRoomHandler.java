package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomService;

@Component
public class DeleteRoomHandler implements RoomEventHandler{
    private final RoomService roomService;

    public DeleteRoomHandler(RoomService roomService) {
        this.roomService = roomService;
    }
    @Override
    public EventType getEventType() {
        return EventType.DELETE;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomService.deleteRoom(userDetails, roomId);
    }
}
