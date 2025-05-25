package socket_server.domain.game.model;

import lombok.*;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.meta.GameMeta;

import java.util.List;

/**
 * Game 데이터(Redis에 저장)
 */
@Data
@Builder
@AllArgsConstructor
public class Game {

    private String roomId;
    private GameType gameType;
    private Difficulty difficulty;
    private int currentRound; // 0, 1, 2, 3, 4, 5
    private int totalRounds;
    private int aiScore;
    private List<GamePlayer> gamePlayers;
    private List<Round> rounds;
    private String winner; // AI or Player

    public static Game fromGameMeta(GameMeta gameMeta) {
        return Game.builder().
                roomId(gameMeta.getRoomId()).
                gameType(gameMeta.getGameType()).
                difficulty(gameMeta.getDifficulty()).
                currentRound(gameMeta.getCurrentRound()).
                totalRounds(gameMeta.getTotalRounds()).
                aiScore(gameMeta.getAiScore()).
                build();
    }

}
