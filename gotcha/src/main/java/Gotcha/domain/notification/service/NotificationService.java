package Gotcha.domain.notification.service;

import Gotcha.domain.notification.dto.NotificationRes;
import Gotcha.domain.notification.dto.NotificationSortType;
import Gotcha.domain.notification.dto.NotificationSummaryRes;
import Gotcha.domain.notification.exception.NotificationExceptionCode;
import Gotcha.domain.notification.repository.NotificationRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.notification.Notification;
import gotcha_domain.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationRepository notificationRepository;

    private final Integer NOTIS_PER_PAGE = 5;


    @Transactional(readOnly = true)
    public Page<NotificationSummaryRes> getNotifications(String keyword, NotificationType type, Integer page, NotificationSortType sort){
        Pageable pageable = PageRequest.of(page, NOTIS_PER_PAGE, sort.getSort());

        Page<Notification> notifications;
        if (keyword == null) keyword = "";
        if (type != null) {
            notifications = notificationRepository
                    .findByTypeAndTitleContainingIgnoreCase(type, keyword, pageable);
        } else {
            notifications = notificationRepository
                    .findByTitleContainingIgnoreCase(keyword, pageable);
        }

        return notifications.map(NotificationSummaryRes::fromEntity);
    }

    @Transactional(readOnly = true)
    public NotificationRes getNotificationsById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).
                orElseThrow(() -> new CustomException(NotificationExceptionCode.NOT_FOUND));

        return NotificationRes.fromEntity(notification);
    }


}
