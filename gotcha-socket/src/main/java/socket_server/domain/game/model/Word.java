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
    private String drawerName;
    private Boolean playerWon;
    private Integer score;
    private boolean submitted;
    private String imageURL;
    private List<Guess> aiGuesses;
    private List<Guess> playerGuesses;
    private List<AIPrediction> AIPredictions;

    public static WordMeta fromWord(Word word) {
        return WordMeta.builder().
                wordIndex(word.getWordIndex()).
                word(word.getWord()).
                drawerUuid(word.getDrawerUuid()).
                imageURL(word.getImageURL()).
                playerWon(word.getPlayerWon()).
                score(word.getScore()).
                drawerName(word.getDrawerName()).
                submitted(word.isSubmitted()).
                build();
    }
}
