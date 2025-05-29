package socket_server.domain.lobby.controller;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.exception.ErrorType;
import socket_server.common.validator.SocketFieldValidator;
import socket_server.domain.lobby.dto.JoinRoomReq;
import socket_server.domain.lobby.service.LobbyService;
import socket_server.domain.lobby.dto.CreateRoomReq;

@Controller
@RequiredArgsConstructor
@MessageMapping("/lobby")
public class LobbyController {
    private final SocketFieldValidator fieldValidator;
    private final LobbyService lobbyService;

    @MessageMapping("/create")
    public void createRoom(
            @Payload CreateRoomReq request,
            @AuthenticationPrincipal SecurityUserDetails userDetails
    ) {
        fieldValidator.validateOrThrow(request, ErrorType.LOBBY);
        lobbyService.createRoom(request, userDetails);
    }

    @MessageMapping("/join/{roomId}")
    public void joinRoom(
            @DestinationVariable String roomId,
            @Payload JoinRoomReq joinRoomReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails
    ) {
        lobbyService.joinRoom(roomId, userDetails, joinRoomReq.password());
    }

}
