package socket_server.domain.room.controller;

import gotcha_domain.auth.SecurityUserDetails;
import gotcha_user.service.UserService;
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
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

@Slf4j
@Controller
@MessageMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final SocketFieldValidator fieldValidator;
    private final RoomEventDispatcher dispatcher;
    private final RoomService roomService;

    @MessageMapping("/{roomId}")
    public void room(@DestinationVariable String roomId,
                     @Payload RoomReq request,
                     @AuthenticationPrincipal SecurityUserDetails userDetails) {
        fieldValidator.validateOrThrow(request, ErrorType.ROOM);
        dispatcher.dispatch(request, roomId, userDetails);
    }

    @MessageMapping("/get/summary")
    public void getRoomList(
            @Payload String roomId,
            @AuthenticationPrincipal SecurityUserDetails userDetails){
        roomService.getRoomSummary(roomId, userDetails.getUuid());
    }

}
