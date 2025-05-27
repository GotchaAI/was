package socket_server.domain.game.dto;

import java.time.LocalDateTime;

public record GameRes(
        GameEventType eventType,
        AISaysRes data,
        LocalDateTime eventAt
) {
}
