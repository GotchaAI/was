package socket_server.domain.game.dto;

import socket_server.domain.game.model.Game;

public record GameStartRes(
        Game game,
        String aiSays
) {
}
