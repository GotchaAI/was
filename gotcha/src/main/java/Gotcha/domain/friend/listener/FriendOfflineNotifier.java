package Gotcha.domain.friend.listener;

import Gotcha.domain.friend.repository.FriendRepository;
import gotcha_common.event.UserDisconnectedEvent;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import socket_server.domain.friend.dto.FriendEventType;
import socket_server.domain.friend.service.FriendSocketService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class FriendOfflineNotifier {
    private final UserService userService;
    private final FriendRepository friendRepository;
    private final FriendSocketService friendSocketService;

    @EventListener
    public void handleUserDisconnected(UserDisconnectedEvent event) {
        String userUuid = event.getUserUuid();

        User user;
        try {
            user = userService.findUserByUuid(userUuid);
        } catch (Exception e) {
            log.warn("OFFLINE 처리 중 사용자 조회 실패 (uuid: {}): {}", userUuid, e.getMessage());
            return;
        }

        List<User> friends = friendRepository.findAllByUserId(user.getId()).stream()
                .map(friend -> friend.getOther(user))
                .distinct()
                .toList();

        for (User friend : friends) {
            friendSocketService.sendFriendAlert(user.getUuid(), friend.getUuid(), user.getUuid(), FriendEventType.OFFLINE);
        }

        log.info("{}의 친구들에게 OFFLINE 알림 전송 완료", userUuid);
    }
}
