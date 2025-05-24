package socket_server.domain.game.model;

import java.util.List;

/**
 * 게임 Round 데이터(Redis에 저장)
 */
public class Round {
    private int roundIndex;
    private long drawingEndTime;
    private String roundWinner; // 'AI' or 'Players'
    private List<Word> words;
}
