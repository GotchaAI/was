package socket_server.domain.game.model;

import lombok.*;
import socket_server.domain.game.meta.WordMeta;

import java.util.List;

/**
 * 게임 제시어 데이터(Redis에 저장)
 */
@Data
@Builder
public class Word {
    private int wordIndex;
    private String word;
    private String drawerUuid;
    private List<Guess> guesses;
    private List<AiPrediction> aiPredictions;
    public static WordMeta toWordMeta(Word word) {
        return WordMeta.builder().
                wordIndex(word.getWordIndex()).
                word(word.getWord()).
                drawerUuid(word.getDrawerUuid()).
                build();
    }
}
