
package socket_server.domain.room.handler;


import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.domain.game.service.GameStartService;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;

@Component
@RequiredArgsConstructor
public class GameStartHandler implements RoomEventHandler {

    private final GameStartService gameStartService;
    @Override
    public EventType getEventType() {
        return EventType.START;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        gameStartService.startGame(roomId, userDetails.getUuid());
    }

}
