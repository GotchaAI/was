package Gotcha.domain.notification.service;

import Gotcha.domain.notification.dto.NotificationSortType;
import Gotcha.domain.notification.dto.NotificationSummaryRes;
import Gotcha.domain.notification.entity.Notification;
import Gotcha.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {


    private final NotificationRepository notificationRepository;

    private final Integer NOTIS_PER_PAGE = 10;


    @Transactional(readOnly = true)
    public Page<NotificationSummaryRes> getNotifications(String keyword, Integer page, NotificationSortType sort){
        Pageable pageable = PageRequest.of(page, NOTIS_PER_PAGE);

        Page<Notification> notifications = notificationRepository.getNotifications(keyword, pageable, sort);

        return notifications.map(NotificationSummaryRes::fromEntity);
    }



}
