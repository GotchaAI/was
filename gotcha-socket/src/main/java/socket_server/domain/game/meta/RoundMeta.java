package socket_server.domain.game.meta;

import lombok.Builder;
import lombok.Data;
import socket_server.domain.game.model.Round;

import java.time.LocalDateTime;

@Data
@Builder
public class RoundMeta {
    private int roundIndex;
    private LocalDateTime drawingEndTime;
    private int currentWordIndex;
    private String roundWinner;

    public static Round toRound(RoundMeta roundMeta) {
        return Round.builder().
                roundIndex(roundMeta.getRoundIndex()).
                drawingEndTime(roundMeta.getDrawingEndTime()).
                currentWordIndex(roundMeta.getCurrentWordIndex()).
                roundWinner(roundMeta.getRoundWinner()).
                build();
    }
}
