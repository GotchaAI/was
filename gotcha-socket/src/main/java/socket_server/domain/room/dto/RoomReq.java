package socket_server.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomReq(
    @NotNull
    EventType eventType,
    String content
) {
}
