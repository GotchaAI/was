
package socket_server.domain.room.handler;


import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.domain.game.service.GameService;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;

@Component
@RequiredArgsConstructor
public class GameStartRequestHandler implements RoomEventHandler {

    private final GameService gameService;

    @Override
    public EventType getEventType() {
        return EventType.GAME_START_REQUEST;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        gameService.startGame(roomId, userDetails.getUuid(), request.content());
    }

}
