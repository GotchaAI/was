package socket_server.domain.game.service;


import gotcha_common.exception.CustomException;
import gotcha_common.util.RedisUtil;
import gotcha_domain.gamehistory.GameHistory;
import gotcha_domain.user.Role;
import gotcha_domain.user.User;
import gotcha_ranking.dto.RankingUserRes;
import gotcha_ranking.service.RankingRedisService;
import gotcha_user.service.UserService;
import gotcha_user.util.LevelExpProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;
import socket_server.gamehistory.service.GameHistoryService;
import socket_server.gamehistory.service.RoundHistoryService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gotcha_common.redis.RedisProperties.GUEST_TTL_SECONDS;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameEndService {

    private final GameHistoryService gameHistoryService;
    private final RoundHistoryService roundHistoryService;
    private final GameBroadCaster gameBroadCaster;
    private final UserService userService;
    private final RankingRedisService rankingRedisService;
    private final ErrorType GAME_ERROR = ErrorType.GAME;
    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final RoundRepository roundRepository;
    private final TaskScheduler taskScheduler;
    private final RedisUtil redisUtil;


    public void updateScore(Game game){
        if(!game.getGameStatus().canHandleEvent(GameEventType.SCORE_UPDATE))
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_STATUS);


        List<GamePlayer> gamePlayers = game.getGamePlayers();
        List<Map<String, Object>> scoreUpdates = new ArrayList<>();

        for(GamePlayer gamePlayer : gamePlayers) {
            Map<String, Object> scoreUpdate = new HashMap<>();
            User user = userService.getUserByUuidAllowingGuest(gamePlayer.getPlayerUuid());
            long newExp = user.getExp() + game.getPlayerScore();
            int newLevel = LevelExpProvider.getLevelByExp((int) newExp);
            boolean levelUp = newLevel > user.getLevel();

            // 변경 적용
            user.setExp(newExp);
            user.setLevel(newLevel);

            if (user.getRole() == Role.GUEST) {
                // 게스트는 Redis에 저장
                redisUtil.setData("guest::" + user.getUuid(), user);
                redisUtil.setDataExpire("guest::" + user.getUuid(), GUEST_TTL_SECONDS);
            } else {
                // 일반 유저는 DB 저장
                if (levelUp) {
                    userService.updateUserLevel(user, newLevel);
                }
                userService.updateUserExp(user, newExp);
                rankingRedisService.updateUserExpRanking(user.getId(), newExp);
                RankingUserRes rankingUserRes = rankingRedisService.getUserRank(user.getId());

                scoreUpdate.put("scoreUpdate", rankingUserRes);
            }

            scoreUpdate.put("levelUp", levelUp);
            scoreUpdates.add(scoreUpdate);
        }

        gameBroadCaster.broadcastGameEvent("SYSTEM", game.getRoomId(), GameEventType.SCORE_UPDATE, scoreUpdates, null);

        saveGameHistory(game);
    }

    public void flushGame(GameMeta gameMeta) {
        log.info("Flushing game data for roomId: {}", gameMeta.getRoomId());
        //1. roomId 기반 삭제
        gameRepository.deleteGameMeta(gameMeta.getRoomId());

        //2. players 삭제
        gamePlayerRepository.deletePlayersByRoomId(gameMeta.getRoomId());

        //3. roundmeta 삭제, totalrounds 만큼 반복
        roundRepository.deleteRoundMetas(gameMeta.getRoomId());

        //4. roundmeta 마다 wordmeta 삭제
        for(int i = 0; i < gameMeta.getTotalRounds(); i++){
            roundRepository.deleteWordMetas(gameMeta.getRoomId(), i);
            //5. wordmeta마다 aiguess, playerguess, aiprediction 삭제
            for(int j = 0; j < 2; j++){
                roundRepository.deleteAIGuesses(gameMeta.getRoomId(), i, j);
                roundRepository.deletePlayerGuesses(gameMeta.getRoomId(), i, j);
                roundRepository.deleteAIPredictions(gameMeta.getRoomId(), i, j);
            }
        }
    }


    public void saveGameHistory(Game game) {
        List<User> users = game.getGamePlayers().stream()
                .map(gp -> {
                    try {
                        return userService.findUserByUuid(gp.getPlayerUuid());
                    } catch (CustomException e) {
                        if ("USER-404-001".equals(e.getExceptionCode().getCode())) {
                            log.warn("게스트 유저이므로 히스토리 저장 제외: {}", gp.getPlayerUuid());
                            return null;
                        }
                        throw e;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        GameHistory gameHistory = gameHistoryService.createGameHistoryWithUsers(game, users);
        log.info("GameHistory Saved: {}", gameHistory);


        game.getRounds().forEach(
                round -> log.info("RoundHistory Saved: {}", roundHistoryService.createRoundHistory(gameHistory, round)));

        flushGame(GameMeta.fromGame(game));
    }


    public void handleDisconnectGame(String roomId, String userUuid) {
        Map<Object, Object> gameMetaMap = gameRepository.findGameMeta(roomId);
        if(gameMetaMap.isEmpty()) {
            // 게임 진행중 아니라면
            log.info("게임 진행 중 아님 ! ! ! !");
            return;
        }

        GameMeta gameMeta = GameMeta.fromRedisMap(roomId, gameMetaMap);

        if(gameMeta.getGameStatus().equals(GameStatus.GAME_ENDED)) {
            // 이미 끝난 게임
            log.info("게임 진행 중 아님 ! ! ! !");
            return;
        }

        // gameMeta 상태를 DISCONNECTED로 바꿔서 게임 더 이상 진행 못하게 막은 다음
        gameMeta.setGameStatus(GameStatus.DISCONNECTED);
        gameRepository.saveGameMeta(gameMeta);
        log.info("게임 상태 변경 ! ! ! ! !");

        // 10초 후 flushGame()
        taskScheduler.schedule(() -> flushGame(gameMeta), Instant.now().plusSeconds(10));

        // 연결 끊김 상태 BROADCAST
        gameBroadCaster.broadcastDisconnectEvent(userUuid, roomId);

    }

}
