package Gotcha.domain.notification.service;

import Gotcha.common.exception.CustomException;
import Gotcha.domain.notification.dto.NotificationReq;
import Gotcha.domain.notification.entity.Notification;
import Gotcha.domain.notification.exception.NotificationExceptionCode;
import Gotcha.domain.notification.repository.NotificationRepository;
import Gotcha.domain.user.entity.Role;
import Gotcha.domain.user.entity.User;
import Gotcha.domain.user.exceptionCode.UserExceptionCode;
import Gotcha.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(NotificationReq notificationReq, Long writerId){
        User writer = validateAdmin(writerId);

        Notification notification = notificationReq.toEntity(writer);

        notificationRepository.save(notification);
    }


    @Transactional
    public void updateNotification(NotificationReq notificationReq, Long notificationId, Long userId){
        validateAdmin(userId);

        Notification notification = validateUserNotification(notificationId, userId);

        notification.update(notificationReq);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId){
        validateAdmin(userId);

        Notification notification = validateUserNotification(notificationId, userId);

        notificationRepository.delete(notification);
    }

    private Notification validateUserNotification(Long notificationId, Long userId) {
        Notification notification = validateNotification(notificationId);

        if(!notification.getWriter().getId().equals(userId))
            throw new CustomException(NotificationExceptionCode.UNAUTHORIZED_ACTION);

        return notification;
    }

    private User validateAdmin(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_ID));
        if(!user.getRole().equals(Role.ADMIN))
            throw new CustomException(NotificationExceptionCode.UNAUTHORIZED_ACTION);
        return user;
    }

    private Notification validateNotification(Long notificationId){
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NotificationExceptionCode.NOT_FOUND));
    }

}
