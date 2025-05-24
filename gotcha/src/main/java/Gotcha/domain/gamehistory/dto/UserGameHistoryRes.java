package Gotcha.domain.gamehistory.dto;

import gotcha_domain.gamehistory.RoundHistory;
import lombok.Builder;

import java.util.List;

@Builder
public record UserGameHistoryRes(
        int round,
        String topic,
        boolean isSuccess,
        double similarity,
        List<String> prediction,
        String picture
) {
    public static UserGameHistoryRes from(RoundHistory roundHistory) {
        return UserGameHistoryRes.builder()
                .round(roundHistory.getRound())
                .topic(roundHistory.getTopic())
                .isSuccess(roundHistory.getIsSuccess())
                .similarity(roundHistory.getSimilarity())
                .prediction(roundHistory.getPrediction())
                .picture(roundHistory.getPicture())
                .build();
    }
}
