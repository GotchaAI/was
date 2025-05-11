package Gotcha.domain.game.dto;

import Gotcha.domain.game.validator.ValidMaxUser;
import gotcha_domain.game.Difficulty;
import gotcha_domain.game.GameType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@ValidMaxUser
public record GameRoom(
        @Schema(description = "제목", example = "인공지능 덤벼")
        @NotBlank(message = "제목은 필수 입력입니다.")
        String title,

        @Schema(description = "최대 인원수", example = "4")
        @NotNull(message = "최대 인원수는 필수 입력입니다.")
        Integer maxUser,

        @Schema(description = "게임 유형", example = "속여라")
        @NotNull(message = "게임 유형은 필수 입력입니다.")
        GameType gameType,

        Integer curUser,

        @Schema(description = "비밀번호", example = "1234")
        @Pattern(regexp = "^[0-9]{4}$")
        String password,

        @Schema(description = "인공지능 난이도", example = "Easy")
        @NotNull(message = "인공지능 난이도는 필수 입력입니다.")
        Difficulty difficulty,

        @Schema(description = "라운드 수", example = "Round1")
        @NotNull(message = "라운드 수는 필수 입력입니다.")
        Rounds rounds,

        GameRoomState state
) {
}
