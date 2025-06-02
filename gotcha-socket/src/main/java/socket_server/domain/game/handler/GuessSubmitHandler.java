package socket_server.domain.game.handler;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.GameReq;
import socket_server.domain.game.dto.GuessSubmitReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.service.GuessFlowService;

@Component
@RequiredArgsConstructor
public class GuessSubmitHandler implements GameEventHandler {

    private final JsonSerializer jsonSerializer;
    private final GuessFlowService guessFlowService;

    @Override
    public GameEventType getEventType() {
        return GameEventType.GUESS_SUBMIT;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, GameReq request) {
        GuessSubmitReq guessSubmitReq =  jsonSerializer.deserialize(request.data(), GuessSubmitReq.class, getErrorType());
        guessFlowService.handlePlayerGuessSubmit(roomId, guessSubmitReq, userDetails.getUuid());
    }
}
