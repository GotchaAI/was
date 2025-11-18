package Gotcha.domain.notification.repository;

import gotcha_domain.notification.Notification;
import gotcha_domain.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Notification> findByTypeAndTitleContainingIgnoreCase(NotificationType type, String keyword, Pageable pageable);
}
