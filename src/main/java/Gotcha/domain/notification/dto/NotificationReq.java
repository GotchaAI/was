package Gotcha.domain.notification.dto;

import Gotcha.domain.notification.entity.Notification;
import Gotcha.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public record NotificationReq(
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        String title,

        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        String content
) {
        public Notification toEntity(User writer) {
                return Notification.builder().
                        title(title).
                        content(content).
                        writer(writer).
                        isFixed(Boolean.FALSE).
                        build();
        }
}