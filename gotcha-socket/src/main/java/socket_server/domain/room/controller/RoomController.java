package socket_server.domain.room.controller;

import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.handler.RoomEventDispatcher;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

@Slf4j
@Controller
@MessageMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomUserService roomUserService;
    private final RoomEventDispatcher dispatcher;

    @MessageMapping("/create")
    public void createRoom(@Valid @Payload CreateRoomRequest request, @AuthenticationPrincipal SecurityUserDetails userDetails) {
        roomService.handleCreateRoom(request, userDetails);
    }

    @MessageMapping("/{roomId}")
    public void room(@DestinationVariable String roomId,
                     @Valid @Payload RoomReq request,
                     @AuthenticationPrincipal SecurityUserDetails userDetails) {
        dispatcher.dispatch(request, roomId, userDetails);
    }

    public void joinRoom(String roomId, String userUuid, String nickname){
        roomUserService.joinAndBroadcast(roomId, userUuid, nickname);
    }
}
