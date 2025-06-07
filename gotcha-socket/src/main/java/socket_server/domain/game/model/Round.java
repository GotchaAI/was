package socket_server.domain.game.model;

import lombok.*;
import socket_server.domain.game.meta.RoundMeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 게임 Round 데이터(Redis에 저장)
 */
@Data
@Builder
public class Round {
    private int roundIndex;
    private LocalDateTime drawingEndTime;
    private int currentWordIndex;
    private List<Word> words;

    public static RoundMeta toRoundMeta(Round round) {
        return RoundMeta.builder().
                roundIndex(round.getRoundIndex()).
                drawingEndTime(round.getDrawingEndTime()).
                currentWordIndex(round.getCurrentWordIndex()).
                build();
    }

}
