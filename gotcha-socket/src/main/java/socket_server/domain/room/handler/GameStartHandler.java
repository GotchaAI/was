
package socket_server.domain.room.handler;


import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.service.GameFlowManager;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.model.RoomEventType;

/**
 * 게임 시작 이벤트를 처리하는 핸들러 클래스.
 * 기존 GameStartService 대신 GameFlowManager를 사용하여 게임 로직을 상태 패턴 기반으로 위임합니다.
 */
@Component
@RequiredArgsConstructor
public class GameStartHandler implements RoomEventHandler {

    private final GameFlowManager gameFlowManager;
    @Override
    public RoomEventType getEventType() {
        return RoomEventType.START;
    }

    /**
     * 게임 시작 요청을 처리합니다.
     * GameFlowManager를 통해 해당 방의 GameContext를 가져와 GAME_START 이벤트를 처리하도록 위임합니다.
     * @param roomId 게임방 ID
     * @param userDetails 요청한 유저의 상세 정보
     * @param request 요청 데이터
     */
    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq request) {
        gameFlowManager.getGameContext(roomId)
            .handleEvent(GameEventType.GAME_START, userDetails.getUuid())
            .subscribe(); // 비동기 작업 실행
    }

}
