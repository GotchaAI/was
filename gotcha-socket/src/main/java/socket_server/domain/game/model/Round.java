package socket_server.domain.game.model;

import java.util.List;

/**
 * 게임 Round 데이터(Redis에 저장)
 */
public class Round {
    int roundIndex;
    String roundWinner;
    List<Word> words;
    List<Guess> guesses;
}
