package socket_server.domain.game.meta;

import lombok.Builder;
import lombok.Data;
import socket_server.domain.game.model.Round;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class RoundMeta {
    private int roundIndex;
    private LocalDateTime drawingEndTime;
    private int currentWordIndex;

    public static Round toRound(RoundMeta roundMeta) {
        return Round.builder().
                roundIndex(roundMeta.getRoundIndex()).
                drawingEndTime(roundMeta.getDrawingEndTime()).
                currentWordIndex(roundMeta.getCurrentWordIndex()).
                build();
    }
}
