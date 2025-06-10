package socket_server.domain.room.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import socket_server.common.validator.ValidPassword;
import socket_server.domain.game.enumType.Difficulty;

@ValidPassword
public record RoomUpdateReq(
        @NotBlank(message = "제목은 필수 입력입니다.")
        String title,

//        @NotNull
//        boolean hasPassword,
//
//        String password,

        @NotNull(message = "인공지능 난이도는 필수 입력입니다.")
        Difficulty difficulty,

        @Min(1) @Max(5)
        int roundCount
) {
}

