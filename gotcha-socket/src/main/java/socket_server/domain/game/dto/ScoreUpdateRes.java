package socket_server.domain.game.dto;

import lombok.Builder;

@Builder
public record ScoreUpdateRes(
        boolean levelUp,
        String nickname,
        int rank,
        long exp,
        int level
) {
}
