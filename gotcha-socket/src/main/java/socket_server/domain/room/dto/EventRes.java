package socket_server.domain.room.dto;

import java.time.LocalDateTime;

public record EventRes(
        EventType eventType,
        Object data,
        LocalDateTime eventAt
) {
}
