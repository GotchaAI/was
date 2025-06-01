package gotcha_domain.gamehistory;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordInfo {
    private Integer wordIndex;
    private String word;
    private String drawerUuid;
    private Boolean submitted;
    private String imageUrl;
    private List<GuessInfo> aiGuesses;
    private List<GuessInfo> playerGuesses;
    private List<AIPredictionInfo> aiPredictions;
}