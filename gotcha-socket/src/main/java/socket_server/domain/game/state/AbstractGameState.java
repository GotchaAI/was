package socket_server.domain.game.state;

import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.service.GameFlowManager;
import java.util.Map;

/**
 * 모든 구체적인 게임 상태 클래스들이 상속받는 추상 기본 클래스.
 * 상태 로직에서 공통적으로 사용되는 헬퍼 메서드들을 정의합니다.
 */
public abstract class AbstractGameState implements GameState {
    /**
     * roomId를 통해 게임 메타데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param manager GameFlowManager 인스턴스 (서비스 접근용)
     * @return 조회된 GameMeta 객체
     * @throws SocketCustomException 게임 ID가 유효하지 않을 경우 발생
     */
    protected GameMeta getGameMetaByRoomId(String roomId, GameFlowManager manager) {
        Map<Object, Object> gameMetaMap = manager.getGameRepository().findGameMeta(roomId);
        if(gameMetaMap.isEmpty()) {
            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.INVALID_GAME_ID);
        }
        return GameMeta.fromRedisMap(roomId, gameMetaMap);
    }
}
