package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomUserService;

@Component
public class JoinRoomHandler implements RoomEventHandler{
    private final RoomUserService roomUserService;

    public JoinRoomHandler(RoomUserService roomUserService) {
        this.roomUserService = roomUserService;
    }

    @Override
    public EventType getEventType() {
        return EventType.JOIN;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        roomUserService.joinAndBroadcast(roomId, userDetails.getUuid(), userDetails.getNickname() ,request.content());
    }
}
