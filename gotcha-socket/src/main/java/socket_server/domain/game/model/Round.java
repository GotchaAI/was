package socket_server.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 게임 Round 데이터(Redis에 저장)
 */
@AllArgsConstructor
@Getter
public class Round {
    private int roundIndex;
    @Setter
    private Long drawingEndTime;
    @Setter
    private String roundWinner; // 'AI' or 'Players'
    private List<Word> words;
}
