
package socket_server.domain.room.handler;


import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.domain.game.service.GameFlowService;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;

@Component
@RequiredArgsConstructor
public class GameStartHandler implements RoomEventHandler {

    private final GameFlowService gameFlowService;

    @Override
    public EventType getEventType() {
        return EventType.START;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        gameFlowService.startGame(roomId, userDetails.getUuid());
    }

}
