package socket_server.domain.game.controller;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.domain.game.dto.GameReq;
import socket_server.domain.game.handler.GameEventDispatcher;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller
@MessageMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameEventDispatcher dispatcher;

    @MessageMapping("/{roomId}")
    public void game(@DestinationVariable String roomId,
                     @Payload GameReq request,
                     @AuthenticationPrincipal SecurityUserDetails userDetails){
        dispatcher.dispatch(request, roomId, userDetails);
    }


}
