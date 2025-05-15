package Gotcha.domain.game.dto;

import gotcha_domain.game.UserGame;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserGameHistoryDetailRes(
        Long gameId,
        String gameType,
        String difficulty,
        LocalDateTime playedAt,
        int score,
        List<UserGameHistoryRes> rounds
) {
    public static UserGameHistoryDetailRes from(UserGame userGame) {
        return UserGameHistoryDetailRes.builder()
                .gameId(userGame.getGame().getId())
                .gameType(String.valueOf(userGame.getGame().getGameType()))
                .difficulty(String.valueOf(userGame.getGame().getDifficulty()))
                .playedAt(userGame.getGame().getCreatedAt())
                .score(userGame.getGame().getScore())
                .rounds(userGame.getGame().getRoundHistories().stream()
                        .map(UserGameHistoryRes::from)
                        .toList())
                .build();
    }
}