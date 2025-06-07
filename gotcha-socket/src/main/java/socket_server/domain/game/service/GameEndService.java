package socket_server.domain.game.service;

import gotcha_common.ranking.dto.RankingUserRes;
import gotcha_common.ranking.service.RankingRedisService;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import gotcha_user.util.LevelExpProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class GameEndService {

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


}
