package socket_server.domain.game.model;

import java.util.List;

/**
 * 게임 제시어 데이터(Redis에 저장)
 */
public class Word {
    int wordIndex;
    String word;
    String drawerUuid;
    List<String> guessOrder;
    List<AiPrediction> aiPredictions;
}
