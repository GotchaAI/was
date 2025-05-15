package socket_server.domain.room.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoomFieldRequest(
        @NotBlank String field,
        @NotBlank String value
) {}
