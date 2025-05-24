package socket_server.domain.game.handler;

import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
import org.springframework.stereotype.Component;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.domain.game.dto.GameEventType;
import socket_server.domain.game.dto.GameReq;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GameEventDispatcher {
    private final Map<GameEventType, GameEventHandler> handlers;

    public GameEventDispatcher(List<GameEventHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(GameEventHandler::getEventType, h -> h));
    }

    public void dispatch(GameReq request, String gameId, SecurityUserDetails userDetails) {
        GameEventHandler handler = handlers.get(request.gameEventType());

        if (handler == null) {
            throw new CustomException(GameExceptionCode.INVALID_EVENT_TYPE);
        }

        handler.handle(gameId, userDetails, request);
    }
}
