package socket_server.domain.game.dto;

public record GameReq(
        GameEventType gameEventType,
        String data
) {
}
