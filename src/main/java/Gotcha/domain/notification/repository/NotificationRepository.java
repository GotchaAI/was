package Gotcha.domain.notification.repository;

import Gotcha.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
