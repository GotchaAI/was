package socket_server.domain.room.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import socket_server.domain.game.enumType.Difficulty;

public record RoomUpdateReq(
        @NotBlank(message = "제목은 필수 입력입니다.")
        String title,

        @NotNull
        boolean hasPassword,

        @Pattern(regexp = "^[0-9]{4}$")
        String password,

        @NotNull(message = "인공지능 난이도는 필수 입력입니다.")
        Difficulty difficulty,

        @Min(1) @Max(5)
        int roundCount
) {
}

