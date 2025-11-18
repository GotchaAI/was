package Gotcha.domain.notification.dto;


import gotcha_domain.notification.Notification;
import gotcha_domain.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationRes(
    String title,
    String content,
    NotificationType type,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    String writer
) {
    public static NotificationRes fromEntity(Notification noti) {
        return new NotificationRes(
            noti.getTitle(),
            noti.getContent(),
            noti.getType(),
            noti.getCreatedAt(),
            noti.getModifiedAt(),
            noti.getWriter().getNickname()
        );
    }
}
