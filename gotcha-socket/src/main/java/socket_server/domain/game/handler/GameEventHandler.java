package socket_server.domain.game.handler;

import socket_server.domain.game.dto.GameEventType;

public interface GameEventHandler {
    GameEventType getEventType();

    void handle();

}
