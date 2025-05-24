package Gotcha.domain.gamehistory.dto;

import gotcha_domain.gamehistory.UserGameHistory;
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
    public static UserGameHistoryDetailRes from(UserGameHistory userGameHistory) {
        return UserGameHistoryDetailRes.builder()
                .gameId(userGameHistory.getGameHistory().getId())
                .gameType(String.valueOf(userGameHistory.getGameHistory().getGameType()))
                .difficulty(String.valueOf(userGameHistory.getGameHistory().getDifficulty()))
                .playedAt(userGameHistory.getGameHistory().getCreatedAt())
                .score(userGameHistory.getGameHistory().getScore())
                .rounds(userGameHistory.getGameHistory().getRoundHistories().stream()
                        .map(UserGameHistoryRes::from)
                        .toList())
                .build();
    }
}