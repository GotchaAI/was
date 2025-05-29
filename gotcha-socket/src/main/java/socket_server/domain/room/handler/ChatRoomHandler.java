package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomService;

@Component
public class ChatRoomHandler implements RoomEventHandler {

    private final RoomService roomService;

    public ChatRoomHandler(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public RoomEventType getEventType() {
        return RoomEventType.CHAT;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomService.sendRoomChat(roomId, userDetails, request.content(), getErrorType());
    }
}
