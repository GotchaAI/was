package socket_server.domain.game.model;

import java.util.List;

/**
 * 게임 제시어 데이터(Redis에 저장)
 */
public class Word {
    private int wordIndex;
    private String word;
    private String drawerUuid;
    private List<String> guessOrder;
    private List<Guess> guesses;
    private List<AiPrediction> aiPredictions;
}
