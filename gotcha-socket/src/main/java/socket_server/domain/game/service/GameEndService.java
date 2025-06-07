package socket_server.domain.game.service;


import gotcha_domain.gamehistory.GameHistory;
import gotcha_domain.user.User;
import gotcha_ranking.dto.RankingUserRes;
import gotcha_user.service.UserService;
import gotcha_user.util.LevelExpProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import gotcha_ranking.service.RankingRedisService;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;
import socket_server.gamehistory.service.GameHistoryService;
import socket_server.gamehistory.service.RoundHistoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class GameEndService {

    private final GameHistoryService gameHistoryService;
    private final RoundHistoryService roundHistoryService;
    private final GameBroadCaster gameBroadCaster;
    private final UserService userService;
    private final RankingRedisService rankingRedisService;

    public void updateScore(Game game){
        List<GamePlayer> gamePlayers = game.getGamePlayers();
        List<Map<String, Object>> scoreUpdates = new ArrayList<>();

        for(GamePlayer gamePlayer : gamePlayers) {
            Map<String, Object> scoreUpdate = new HashMap<>();
            User user = userService.findUserByUuid(gamePlayer.getPlayerUuid());
            long newExp = user.getExp() + game.getPlayerScore();
            int newLevel = LevelExpProvider.getLevelByExp((int) newExp);

            boolean levelUp = newLevel > user.getLevel();
            if (levelUp) {
                userService.updateUserLevel(user, newLevel);
            }
            userService.updateUserExp(user, newExp);
            rankingRedisService.updateUserExpRanking(user.getId(), newExp);
            RankingUserRes rankingUserRes = rankingRedisService.getUserRank(user.getId());

            scoreUpdate.put("levelUp", levelUp);
            scoreUpdate.put("scoreUpdate", rankingUserRes);
            scoreUpdates.add(scoreUpdate);
        }

        gameBroadCaster.broadcastGameEvent("SYSTEM", game.getRoomId(), GameEventType.SCORE_UPDATE, scoreUpdates, null);
    }

    public void saveGame(Game game) {
        List<User> users = game.getGamePlayers().stream().map(
                gamePlayer -> userService.findUserByUuid(gamePlayer.getPlayerUuid())
        ).toList();

        GameHistory gameHistory = gameHistoryService.createGameHistoryWithUsers(game, users);
        log.info("GameHistory Saved: {}", gameHistory);


        game.getRounds().forEach(
                round -> log.info("RoundHistory Saved: {}", roundHistoryService.createRoundHistory(gameHistory, round)));

    }


}
