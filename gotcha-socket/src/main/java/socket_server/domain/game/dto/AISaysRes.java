package socket_server.domain.game.dto;

public record AISaysRes(
        Object gameData,
        String aiSays
) {
}
