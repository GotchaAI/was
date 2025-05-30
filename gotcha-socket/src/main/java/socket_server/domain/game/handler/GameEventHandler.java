package socket_server.domain.game.handler;

import gotcha_domain.auth.SecurityUserDetails;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.dto.GameReq;

public interface GameEventHandler {
    GameEventType getEventType();

    void handle(String roomId, SecurityUserDetails userDetails, GameReq request);
}
