package socket_server.domain.room.controller;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.exception.ErrorType;
import socket_server.common.validator.SocketFieldValidator;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.handler.RoomEventDispatcher;

@Slf4j
@Controller
@MessageMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final SocketFieldValidator fieldValidator;
    private final RoomEventDispatcher dispatcher;

    @MessageMapping("/{roomId}")
    public void room(@DestinationVariable String roomId,
                     @Payload RoomReq request,
                     @AuthenticationPrincipal SecurityUserDetails userDetails) {
        fieldValidator.validateOrThrow(request, ErrorType.ROOM);
        dispatcher.dispatch(request, roomId, userDetails);
    }

}
