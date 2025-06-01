package socket_server.domain.game.meta;

import lombok.Builder;
import lombok.Data;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.game.model.Game;

import java.util.Map;

@Data
@Builder
public class GameMeta {
    private String roomId;
    private GameType gameType;
    private Difficulty difficulty;
    private GameStatus gameStatus;
    private String winner; // AI or Player
    private int currentRound; // 1, 2, 3, 4, 5
    private int totalRounds;

    public static GameMeta fromRedisMap(String roomId, Map<Object, Object> map) {
        return GameMeta.builder().
                roomId(roomId).
                gameType(GameType.valueOf((String) map.get("gameType"))).
                gameStatus(GameStatus.valueOf((String) map.get("gameStatus"))).
                difficulty(Difficulty.valueOf((String) map.get("difficulty"))).
                currentRound(Integer.parseInt((String) map.get("currentRound"))).
                totalRounds(Integer.parseInt((String) map.get("totalRounds"))).
                build();
    }

    public static GameMeta fromGame(Game game) {
        return GameMeta.builder().
                roomId(game.getRoomId()).
                gameType(game.getGameType()).
                difficulty(game.getDifficulty()).
                gameStatus(game.getGameStatus()).
                currentRound(game.getCurrentRound()).
                totalRounds(game.getTotalRounds()).
                build();
    }

}
