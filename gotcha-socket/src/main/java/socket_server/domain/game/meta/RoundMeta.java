package socket_server.domain.game.meta;

import lombok.Builder;
import lombok.Data;
import socket_server.domain.game.model.Round;

@Data
@Builder
public class RoundMeta {
    private int roundIndex;
    private Long drawingEndTime;
    private String roundWinner;

    public static Round toRound(RoundMeta roundMeta) {
        return Round.builder().
                roundIndex(roundMeta.getRoundIndex()).
                drawingEndTime(roundMeta.getDrawingEndTime()).
                roundWinner(roundMeta.getRoundWinner()).
                build();
    }
}
