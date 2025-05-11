package gotcha_domain.notification;

import gotcha_domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record NotificationReq(
        @Schema(description = "공지사항 제목", example = "걍 공지사항이다")
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        String title,

        @Schema(description = "공지사항 내용", example = "걍 공지사항이다 인마")
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        String content
) {
        public Notification toEntity(User writer) {
                return Notification.builder().
                        title(title).
                        content(content).
                        writer(writer).
                        build();
        }
}