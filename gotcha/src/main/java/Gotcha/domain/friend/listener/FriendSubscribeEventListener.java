package Gotcha.domain.friend.listener;

import Gotcha.domain.friend.repository.FriendRepository;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import socket_server.domain.friend.dto.FriendEventType;
import socket_server.domain.friend.service.FriendSocketService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendSubscribeEventListener implements ApplicationListener<SessionSubscribeEvent> {

    private final UserService userService;
    private final FriendRepository friendRepository;
    private final FriendSocketService friendSocketService;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getDestination() == null) return;
        String destination = accessor.getDestination(); // 예: /sub/friend/2

        // 1. /sub/friend/{uuid} 패턴인지 확인
        if (!destination.startsWith("/sub/friend/")) return;

        String uuid = destination.substring("/sub/friend/".length());

        // 2. 사용자 정보 조회
        User user = userService.findUserByUuid(uuid);

        // 3. 친구 조회
        List<User> friends = friendRepository.findAllByUserId(user.getId()).stream()
                .map(friend -> friend.getOther(user))
                .distinct()
                .toList();

        // 4. 각 친구에게 ONLINE 알림
        for (User friend : friends) {
            friendSocketService.sendFriendAlert(user.getUuid(), friend.getUuid(), user.getUuid(), FriendEventType.ONLINE);
        }

        log.info("{}의 친구들에게 ONLINE 알림 전송 완료", uuid);
    }
}
