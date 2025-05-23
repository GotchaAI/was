package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;

public interface RoomEventHandler {
    EventType getEventType();

    void handle(String roomId, SecurityUserDetails userDetails, RoomReq request);
}
