package socket_server.domain.game.state;

import reactor.core.publisher.Mono;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.service.GameFlowManager;
import java.time.Duration;

/**
 * 게임의 라운드 종료 상태를 나타내는 클래스.
 * 이 상태에서는 라운드 종료(ROUND_END) 이벤트를 처리합니다.
 */
public class RoundEndState extends AbstractGameState {

    /**
     * 이벤트를 처리합니다.
     * ROUND_END 이벤트가 발생하면 다음 라운드를 시작하거나 게임을 종료합니다.
     * @param context 게임 컨텍스트
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 인자 (사용되지 않음)
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    @Override
    public Mono<Void> handleEvent(GameContext context, GameEventType eventType, String... args) {
        if (eventType == GameEventType.ROUND_END) {
            GameFlowManager manager = context.getGameFlowManager();
            GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

            gameMeta.setGameStatus(GameStatus.ROUND_ENDED);
            gameMeta.setCurrentRound(gameMeta.getCurrentRound() + 1);
            manager.getGameRepository().saveGameMeta(gameMeta);

            // 다음 라운드가 남아있으면 다음 라운드 시작, 아니면 게임 종료
            if (gameMeta.getCurrentRound() < gameMeta.getTotalRounds()) {
                context.setState(new DrawingPhaseState()); // 다음 라운드는 다시 그리기 단계부터 시작
                return Mono.delay(Duration.ofSeconds(1)).then(context.handleEvent(GameEventType.ROUND_START));
            } else {
                context.setState(new GameEndState()); // 모든 라운드 종료 시 게임 종료
                return Mono.delay(Duration.ofSeconds(1)).then(context.handleEvent(GameEventType.GAME_END));
            }
        }
        return GameState.super.handleEvent(context, eventType, args);
    }

    /**
     * 이 상태의 GameStatus를 반환합니다.
     * @return GameStatus.ROUND_ENDED
     */
    @Override
    public GameStatus getStatus() {
        return GameStatus.ROUND_ENDED;
    }
}
