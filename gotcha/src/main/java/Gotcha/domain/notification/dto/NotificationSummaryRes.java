package Gotcha.domain.notification.dto;

import Gotcha.domain.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationSummaryRes(
        Long notificationId,
        String title,
        LocalDateTime createdAt,
        String writer
) {
    public static NotificationSummaryRes fromEntity(Notification noti){
        return new NotificationSummaryRes(
                noti.getId(),
                noti.getTitle(),
                noti.getCreatedAt(),
                noti.getWriter().getNickname()
        );
    }
}
