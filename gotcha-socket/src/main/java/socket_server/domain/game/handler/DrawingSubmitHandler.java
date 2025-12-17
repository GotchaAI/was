package socket_server.domain.game.handler;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.DrawingSubmitReq;
import socket_server.domain.game.dto.GameReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.service.GameFlowManager;

/**
 * 그림 제출 이벤트를 처리하는 핸들러 클래스.
 * 기존 DrawingSubmitService 대신 GameFlowManager를 사용하여 게임 로직을 상태 패턴 기반으로 위임합니다.
 */
@Component
@RequiredArgsConstructor
public class DrawingSubmitHandler implements GameEventHandler {

    private final GameFlowManager gameFlowManager;
    private final JsonSerializer jsonSerializer;

    @Override
    public GameEventType getEventType() {
        return GameEventType.DRAWING_SUBMIT;
    }

    /**
     * 그림 제출 요청을 처리합니다.
     * GameFlowManager를 통해 해당 방의 GameContext를 가져와 DRAWING_SUBMIT 이벤트를 처리하도록 위임합니다.
     * @param roomId 게임방 ID
     * @param userDetails 요청한 유저의 상세 정보
     * @param request 요청 데이터 (그림 URL 포함)
     */
    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, GameReq request) {
        DrawingSubmitReq drawingSubmit = jsonSerializer.deserialize(request.data(), DrawingSubmitReq.class, getErrorType());
        gameFlowManager.getGameContext(roomId)
            .handleEvent(GameEventType.DRAWING_SUBMIT, userDetails.getUuid(), drawingSubmit.imageURL())
            .subscribe(); // 비동기 작업 실행
    }

}

