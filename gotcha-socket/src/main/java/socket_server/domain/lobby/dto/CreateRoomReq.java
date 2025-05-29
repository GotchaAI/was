package socket_server.domain.lobby.dto;


import jakarta.validation.constraints.*;
import socket_server.common.validator.ValidMaxUser;
import socket_server.common.validator.ValidPassword;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;

@ValidMaxUser
@ValidPassword
public record CreateRoomReq(
        @NotBlank(message = "제목은 필수 입력입니다.")
        String title,

        @NotNull(message = "최대 인원수는 필수 입력입니다.")
        Integer maxUser,

        @NotNull
        boolean hasPassword,

        String password,

        @NotNull (message = "인공지능 난이도는 필수 입력입니다.")
        Difficulty difficulty,

        @NotNull(message = "게임 유형은 필수 입력입니다.")
        GameType gameType,

        @Min(1) @Max(5)
        int roundCount
) {}
