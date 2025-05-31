package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomReq;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoomEventDispatcher {
    private final Map<RoomEventType, RoomEventHandler> handlers;

    public RoomEventDispatcher(List<RoomEventHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(RoomEventHandler::getEventType, h -> h));
    }

    public void dispatch(RoomReq request, String roomId, SecurityUserDetails userDetails) {
        RoomEventHandler handler = handlers.get(request.eventType());

        if (handler == null) {
            throw new SocketCustomException(ErrorType.ROOM, RoomExceptionCode.INVALID_EVENT_TYPE);
        }

        handler.handle(roomId, userDetails, request);
    }
}
