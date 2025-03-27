package Gotcha.domain.notification.controller;

import Gotcha.domain.notification.api.NotificationApi;
import Gotcha.domain.notification.dto.NotificationRes;
import Gotcha.domain.notification.dto.NotificationSortType;
import Gotcha.domain.notification.dto.NotificationSummaryRes;
import Gotcha.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getNotificationsById(@PathVariable(value = "notificationId") Long notificationId) {
        NotificationRes notificationRes = notificationService.getNotificationsById(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(notificationRes);
    }
}
