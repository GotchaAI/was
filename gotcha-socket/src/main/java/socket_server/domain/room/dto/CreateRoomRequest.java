package socket_server.domain.room.dto;


import socket_server.domain.game.enumType.AiMode;
import socket_server.domain.game.enumType.GameMode;

public record CreateRoomRequest(
        String title,
        boolean hasPassword,
        String password,
        AiMode aimode,
        GameMode gameMode
) {}
