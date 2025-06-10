package socket_server.gamehistory.dto;

import gotcha_domain.gamehistory.RoundHistory;
import gotcha_domain.gamehistory.WordInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record RoundHistoryRes(
        int roundIndex,
        List<WordInfo> words
) {
    public static RoundHistoryRes from(RoundHistory roundHistory) {
        return RoundHistoryRes.builder()
                .roundIndex(roundHistory.getRoundIndex())
                .words(roundHistory.getWords())
                .build();
    }
}