package socket_server.domain.game.model;

import lombok.Getter;
import lombok.Setter;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.game.enumType.Difficulty;

import java.util.List;

/**
 * Game 데이터(Redis에 저장)
 */
@Setter
@Getter
public class Game {

    private String gameUuid;
    private GameType gameType;
    private Difficulty difficulty;
    private int totalRounds;
    private int aiScore;
    private List<GamePlayer> gamePlayers;
    private List<Round> rounds;
    private String winner; // AI or Player

}
