package socket_server.domain.game.model;

import gotcha_domain.gamehistory.GameHistory;
import lombok.*;
import socket_server.domain.game.enumType.GameStatus;
import gotcha_domain.gamehistory.GameType;
import gotcha_domain.gamehistory.Difficulty;
import socket_server.domain.game.meta.GameMeta;

import java.util.List;
import java.util.Map;

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
    private GameStatus gameStatus;
    private int currentRound; // 0, 1, 2, 3, 4, 5
    private int totalRounds;
    private Boolean playerWon; // AI or Player
    private Integer aiScore;
    private Integer playerScore;
    private List<GamePlayer> gamePlayers;
    private List<Round> rounds;

    public static Game fromGameMeta(GameMeta gameMeta) {
        return Game.builder().
                roomId(gameMeta.getRoomId()).
                gameType(gameMeta.getGameType()).
                difficulty(gameMeta.getDifficulty()).
                currentRound(gameMeta.getCurrentRound()).
                gameStatus(gameMeta.getGameStatus()).
                playerWon(gameMeta.getPlayerWon()).
                aiScore(gameMeta.getAiScore()).
                playerScore(gameMeta.getPlayerScore()).
                totalRounds(gameMeta.getTotalRounds()).
                build();
    }

//    public static GameHistory toGameHistory(Game game) {
//        return GameHistory.builder().
//                gameType(game.getGameType()).
//                difficulty(game.getDifficulty()).
//                gameStatus(game.getGameStatus()).
//                playerWon(game.getPlayerWon()).

}
