
package socket_server.domain.room.handler;


import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.exception.room.RoomExceptionCode;
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
        try {
            int totalRounds = Integer.parseInt(request.content());
            switch(totalRounds){
                case 1:
                case 3:
                case 5:
                    gameService.startGame(roomId, userDetails.getUuid(), totalRounds);
                    break;
                default:
                    throw new CustomException(RoomExceptionCode.INVALID_ROUND_NUMBER);
            }
        } catch (NumberFormatException e) {
            throw new CustomException(RoomExceptionCode.INVALID_ROUND_NUMBER);
        }
  }

}
