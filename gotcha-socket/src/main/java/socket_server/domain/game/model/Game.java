package socket_server.domain.game.model;

import lombok.*;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.game.enumType.Difficulty;

import java.util.List;

/**
 * Game 데이터(Redis에 저장)
 */
@Data
@Builder
@AllArgsConstructor
public class Game {

    private String gameId;
    private GameType gameType;
    private Difficulty difficulty;
    private int currentRound; // 1, 2, 3, 4, 5
    private int totalRounds;
    private int aiScore;
    private List<GamePlayer> gamePlayers;
    private List<Round> rounds;
    private String winner; // AI or Player

}
