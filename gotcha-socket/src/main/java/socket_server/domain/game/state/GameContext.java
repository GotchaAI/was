package socket_server.domain.game.state;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.service.GameFlowManager;

/**
 * 특정 게임 인스턴스(roomId)의 현재 상태와 관련 정보를 담는 컨텍스트 클래스.
 * GameFlowManager를 통해 필요한 서비스에 접근하며, 현재 GameState를 관리합니다.
 */
@Getter
public class GameContext {
    private final String roomId;
    private final GameFlowManager gameFlowManager;
    @Setter
    private GameState currentState;

    /**
     * GameContext의 생성자.
     * @param roomId 이 컨텍스트가 관리할 게임방 ID
     * @param gameFlowManager 게임 흐름 관리자 (서비스 접근용)
     */
    public GameContext(String roomId, GameFlowManager gameFlowManager) {
        this.roomId = roomId;
        this.gameFlowManager = gameFlowManager;
        // 초기 상태 설정
        this.currentState = new PreGameState();
    }

    /**
     * 현재 상태의 handleEvent 메소드를 호출하여 이벤트를 처리합니다.
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 처리 시 필요한 추가 인자들
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    public Mono<Void> handleEvent(GameEventType eventType, String... args) {
        return currentState.handleEvent(this, eventType, args);
    }
}