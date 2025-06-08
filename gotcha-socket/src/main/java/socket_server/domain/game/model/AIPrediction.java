package socket_server.domain.game.model;

import gotcha_domain.gamehistory.AIPredictionInfo;
import lombok.Getter;

@Getter
public class AIPrediction {
    private String predicted;
    private Double confidence;

    public static AIPredictionInfo fromAIPrediction(AIPrediction aiPrediction) {
        return AIPredictionInfo.builder()
                .predicted(aiPrediction.getPredicted())
                .confidence(aiPrediction.getConfidence())
                .build();
    }

}
