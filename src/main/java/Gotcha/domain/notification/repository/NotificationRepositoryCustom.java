package Gotcha.domain.notification.repository;

import Gotcha.domain.notification.dto.NotificationSortType;
import Gotcha.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {
    Page<Notification> getNotifications(String keyword, Pageable pageable, NotificationSortType sort);
}
