package Gotcha.domain.notification.dto;


import gotcha_domain.notification.Notification;

import java.time.LocalDateTime;

public record NotificationRes(
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    String writer
) {
    public static NotificationRes fromEntity(Notification noti) {
        return new NotificationRes(
            noti.getTitle(),
            noti.getContent(),
            noti.getCreatedAt(),
            noti.getModifiedAt(),
            noti.getWriter().getNickname()
        );
    }
}
