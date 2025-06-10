package gotcha_domain.gamehistory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuessInfo {
    private String guesserUuid;
    private String guessWord;
    private Integer attempts;
    private Boolean correct;
}