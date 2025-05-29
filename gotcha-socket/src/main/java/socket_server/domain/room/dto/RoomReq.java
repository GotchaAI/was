package socket_server.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import socket_server.domain.room.model.RoomEventType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomReq(
    @NotNull
    RoomEventType roomEventType,
    String content
) {
}
