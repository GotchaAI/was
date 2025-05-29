package socket_server.domain.room.dto;

import socket_server.domain.room.model.RoomEventType;

import java.time.LocalDateTime;

public record EventRes(
        RoomEventType type,
        Object data,
        LocalDateTime eventAt
) {
}
