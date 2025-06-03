package socket_server.domain.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;

public record RoomListReq(
        @Schema(description = "게임 모드", examples = "TRICK_MYOMYO")
        @NotNull(message = "게임 모드는 필수입니다.")
        GameType gameType,

        @Schema(description = "인공지능 난이도", examples = "BASIC")
        Difficulty difficulty
) {
}
