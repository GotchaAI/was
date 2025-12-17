package socket_server.domain.game.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;
import socket_server.domain.game.state.GameContext;
import socket_server.domain.room.repository.RoomUserRepository;
import socket_server.domain.room.service.RoomUserService;
import socket_server.gamehistory.service.GameHistoryService;
import socket_server.gamehistory.service.RoundHistoryService;
import gotcha_user.service.UserService;
import gotcha_ranking.service.RankingRedisService;
import gotcha_common.util.RedisUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 게임 흐름을 중앙에서 관리하는 서비스 클래스 (Game State Machine의 핵심).
 * 각 게임방(roomId)별 GameContext 인스턴스를 관리하며, 모든 게임 로직에 필요한 서비스들을 제공합니다.
 * 이 클래스는 싱글톤으로 동작하며, 게임의 상태 전이 및 이벤트 처리를 GameContext에 위임합니다.
 */
@Service
@Getter
@RequiredArgsConstructor
public class GameFlowManager {
    // 각 roomId에 해당하는 GameContext를 저장하는 맵
    private final ConcurrentHashMap<String, GameContext> gameContexts = new ConcurrentHashMap<>();

    // 게임 로직 수행에 필요한 모든 서비스들을 주입받습니다.
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GameBroadCaster gameBroadCaster;
    private final AIClientService aiClientService;
    private final JsonSerializer jsonSerializer;
    private final GameEndService gameEndService;
    private final RoomUserService roomUserService;
    private final RoomUserRepository roomUserRepository;
    private final GameHistoryService gameHistoryService;
    private final RoundHistoryService roundHistoryService;
    private final UserService userService;
    private final RankingRedisService rankingRedisService;
    private final RedisUtil redisUtil;
    private final GuessRequestService guessRequestService;

    /**
     * 특정 roomId에 해당하는 GameContext를 반환합니다.
     * 만약 해당 roomId의 GameContext가 없으면 새로 생성하여 맵에 추가합니다.
     * @param roomId 게임방 ID
     * @return 해당 게임방의 GameContext 인스턴스
     */
    public GameContext getGameContext(String roomId) {
        return gameContexts.computeIfAbsent(roomId, id -> new GameContext(id, this));
    }

    /**
     * 특정 roomId에 해당하는 GameContext를 맵에서 제거합니다.
     * 게임 종료 시 호출되어 리소스를 정리합니다.
     * @param roomId 게임방 ID
     */
    public void removeGameContext(String roomId) {
        gameContexts.remove(roomId);
    }
}
