package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import socket_server.common.exception.ErrorType;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomReq;

public interface RoomEventHandler {
    RoomEventType getEventType();

    default ErrorType getErrorType() {
        return ErrorType.ROOM;
    }

    void handle(String roomId, SecurityUserDetails userDetails, RoomReq request);
}
