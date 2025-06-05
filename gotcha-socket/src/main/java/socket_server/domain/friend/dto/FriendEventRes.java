package socket_server.domain.friend.dto;

import java.time.LocalDateTime;

public record FriendEventRes(
        FriendEventType type,
        Object data,
        LocalDateTime eventAt
) {
}
