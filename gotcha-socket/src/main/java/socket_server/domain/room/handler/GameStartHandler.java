
package socket_server.domain.room.handler;


import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.domain.game.service.GameStartService;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.model.RoomEventType;

@Component
@RequiredArgsConstructor
public class GameStartHandler implements RoomEventHandler {

    private final GameStartService gameStartService;
    @Override
    public RoomEventType getEventType() {
        return RoomEventType.START;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        gameStartService.startGame(roomId, userDetails.getUuid(), getErrorType());
    }

}
