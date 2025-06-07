package gotcha_domain.chat;

import java.time.LocalDateTime;

public record ChatMessage(
        String nickname,
        String content,
        ChatType chatType,
        LocalDateTime sentAt
) {
}
