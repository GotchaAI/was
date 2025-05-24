package socket_server.domain.room.dto;


import jakarta.validation.constraints.*;
import socket_server.common.validator.ValidMaxUser;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;

@ValidMaxUser
public record CreateRoomRequest(
        @NotBlank(message = "제목은 필수 입력입니다.")
        String title,

        @NotNull(message = "최대 인원수는 필수 입력입니다.")
        Integer maxUser,

        @NotNull
        boolean hasPassword,

        @Pattern(regexp = "^[0-9]{4}$")
        String password,

        @NotNull (message = "인공지능 난이도는 필수 입력입니다.")
        Difficulty difficulty,

        @NotNull(message = "게임 유형은 필수 입력입니다.")
        GameType gameType,

        @Min(1) @Max(5)
        int roundCount
) {}
