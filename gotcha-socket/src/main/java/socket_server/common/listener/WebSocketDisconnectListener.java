package socket_server.common.listener;

import gotcha_common.event.UserDisconnectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import socket_server.common.util.DisconnectManager;
import socket_server.domain.game.service.GameEndService;
import socket_server.domain.room.service.RoomUserService;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketDisconnectListener {

    private final RoomUserService roomUserService;
    private final DisconnectManager disconnectManager;
    private final GameEndService gameEndService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {

        Principal principal = event.getUser();

        if (principal instanceof Authentication auth) {
            String userUuid = auth.getName();

            log.info(userUuid + " 소켓 연결 해제!");

            if (userUuid == null) return;

            String roomId = roomUserService.findRoomIdByUserUuid(userUuid);

            if (roomId != null) {
                disconnectManager.markDisconnect(userUuid, roomId);

                // 게임 진행중이였다면 종료
                // 나간 플레이어 UUID 함께 보내준다
                gameEndService.handleDisconnectGame(roomId, userUuid);
            }

            eventPublisher.publishEvent(new UserDisconnectedEvent(userUuid));
        }

    }

}

