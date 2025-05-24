package socket_server.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게임 제시어 데이터(Redis에 저장)
 */
@AllArgsConstructor
@Setter
@Getter
public class Word {
    private int wordIndex;
    private String word;
    private String drawerUuid;
    private List<Guess> guesses;
    private List<AiPrediction> aiPredictions;
}
