package Gotcha.domain.game.dto;

import gotcha_domain.game.UserGame;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserGameHistorySummaryRes(
        Long gameId,
        String gameType,
        String difficulty,
        LocalDateTime playedAt,
        int score
) {
    public static UserGameHistorySummaryRes from(UserGame userGame){
        return UserGameHistorySummaryRes.builder()
                .gameId(userGame.getGame().getId())
                .gameType(String.valueOf(userGame.getGame().getGameType()))
                .difficulty(String.valueOf(userGame.getGame().getDifficulty()))
                .playedAt(userGame.getGame().getCreatedAt())
                .score(userGame.getGame().getScore())
                .build();
    }
}
