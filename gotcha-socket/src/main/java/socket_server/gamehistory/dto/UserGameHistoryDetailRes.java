package socket_server.gamehistory.dto;

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
        Boolean playerWon,
        Integer aiScore,
        Integer playerScore,
        List<RoundHistoryRes> rounds
) {
    public static UserGameHistoryDetailRes from(UserGameHistory userGameHistory) {
        return UserGameHistoryDetailRes.builder()
                .gameId(userGameHistory.getGameHistory().getId())
                .gameType(String.valueOf(userGameHistory.getGameHistory().getGameType()))
                .difficulty(String.valueOf(userGameHistory.getGameHistory().getDifficulty()))
                .playedAt(userGameHistory.getGameHistory().getCreatedAt())
                .playerScore(userGameHistory.getGameHistory().getPlayerScore())
                .aiScore(userGameHistory.getGameHistory().getAiScore())
                .playerWon(userGameHistory.getGameHistory().getPlayerWon())
                .rounds(userGameHistory.getGameHistory().getRoundHistories().stream()
                        .map(RoundHistoryRes::from)
                        .toList())
                .build();
    }
}