package gotcha_domain.gamehistory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AIPredictionInfo {
    private String predicted;
    private Double confidence;
}
