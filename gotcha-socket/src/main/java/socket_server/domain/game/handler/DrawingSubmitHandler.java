package socket_server.domain.game.handler;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.DrawingSubmitReq;
import socket_server.domain.game.dto.GameEventType;
import socket_server.domain.game.dto.GameReq;
import socket_server.domain.game.service.GameService;

@Component
@RequiredArgsConstructor
public class DrawingSubmitHandler implements GameEventHandler {

    private final GameService gameService;
    private final JsonSerializer jsonSerializer;

    @Override
    public GameEventType getEventType() {
        return GameEventType.DRAWING_SUBMIT;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, GameReq request) {
        DrawingSubmitReq drawingSubmit = jsonSerializer.deserialize(request.data(), DrawingSubmitReq.class);
        gameService.submitDrawing(roomId, userDetails.getUuid(), drawingSubmit.imageURL());
    }

}
