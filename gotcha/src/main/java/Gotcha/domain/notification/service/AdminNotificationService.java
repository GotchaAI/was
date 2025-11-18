package Gotcha.domain.notification.service;

import Gotcha.domain.notification.exception.NotificationExceptionCode;
import Gotcha.domain.notification.repository.NotificationRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.notification.Notification;
import gotcha_domain.notification.NotificationReq;
import gotcha_domain.user.User;
import gotcha_user.exceptionCode.UserExceptionCode;
import gotcha_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(NotificationReq notificationReq, Long writerId){
        User writer = userRepository.findById(writerId)
            .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        Notification notification = notificationReq.toEntity(writer);

        notificationRepository.save(notification);
    }


    @Transactional
    public void updateNotification(NotificationReq notificationReq, Long notificationId){
        Notification notification = validateNotification(notificationId);

        notification.update(notificationReq);
    }

    @Transactional
    public void deleteNotification(Long notificationId){
        Notification notification = validateNotification(notificationId);

        notificationRepository.delete(notification);
    }

    private Notification validateUserNotification(Long notificationId, Long userId) {
        Notification notification = validateNotification(notificationId);

        if(!notification.getWriter().getId().equals(userId))
            throw new CustomException(NotificationExceptionCode.UNAUTHORIZED_ACTION);

        return notification;
    }

    private Notification validateNotification(Long notificationId){
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NotificationExceptionCode.NOT_FOUND));
    }

}
