package socket_server.domain.room.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import socket_server.domain.game.enumType.AiMode;
import socket_server.domain.game.enumType.GameMode;

public record CreateRoomRequest(
        @NotBlank String title,
        boolean hasPassword,
        @Size(min = 4, max = 20) String password,
        @NotNull AiMode aimode,
        @NotNull GameMode gameMode
) {}
