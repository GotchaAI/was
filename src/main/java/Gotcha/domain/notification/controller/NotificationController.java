package Gotcha.domain.notification.controller;

import Gotcha.domain.notification.api.NotificationApi;
import Gotcha.domain.notification.dto.NotificationSortType;
import Gotcha.domain.notification.dto.NotificationSummaryRes;
import Gotcha.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @GetMapping
    @Override
    public ResponseEntity<?> getNotifications(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page") Integer page,
                                       @RequestParam(value = "sort", defaultValue = "DATE_DESC") NotificationSortType sort){
        Page<NotificationSummaryRes> notifications = notificationService.getNotifications(keyword, page, sort);

        return ResponseEntity.status(HttpStatus.OK).body(notifications);
    }




    @Override
    public ResponseEntity<?> getNotificationsById(Long notificationId) {
        return null;
    }
}
