package socket_server.domain.game.state;

import reactor.core.publisher.Mono;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;

/**
 * 게임 상태를 정의하는 인터페이스.
 * 모든 구체적인 게임 상태 클래스는 이 인터페이스를 구현해야 합니다.
 */
public interface GameState {
    /**
     * 현재 상태에서 특정 이벤트를 처리합니다.
     * 기본 구현은 해당 상태에서 지원하지 않는 이벤트에 대해 UnsupportedOperationException을 발생시킵니다.
     * @param context 게임 컨텍스트 (현재 게임의 모든 정보 및 서비스 접근)
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 처리 시 필요한 추가 인자들
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    default Mono<Void> handleEvent(GameContext context, GameEventType eventType, String... args) {
        return Mono.error(new UnsupportedOperationException("Event " + eventType + " not supported in state " + getStatus()));
    }

    /**
     * 현재 게임 상태의 GameStatus를 반환합니다.
     * @return 현재 게임 상태의 GameStatus
     */
    GameStatus getStatus();
}
