package socket_server.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatMessage(
        String nickname,
        String content,
        ChatType type,
        LocalDateTime sentAt
) {
}
