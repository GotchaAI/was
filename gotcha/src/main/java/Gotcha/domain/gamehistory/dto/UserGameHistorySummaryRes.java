package Gotcha.domain.gamehistory.dto;

import gotcha_domain.gamehistory.UserGameHistory;
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
    public static UserGameHistorySummaryRes from(UserGameHistory userGameHistory){
        return UserGameHistorySummaryRes.builder()
                .gameId(userGameHistory.getGameHistory().getId())
                .gameType(String.valueOf(userGameHistory.getGameHistory().getGameType()))
                .difficulty(String.valueOf(userGameHistory.getGameHistory().getDifficulty()))
                .playedAt(userGameHistory.getGameHistory().getCreatedAt())
                .score(userGameHistory.getGameHistory().getScore())
                .build();
    }
}