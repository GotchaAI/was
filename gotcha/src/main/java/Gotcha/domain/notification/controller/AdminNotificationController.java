package Gotcha.domain.notification.controller;

import Gotcha.domain.notification.api.AdminNotificationApi;
import Gotcha.domain.notification.service.AdminNotificationService;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_common.dto.SuccessRes;
import gotcha_domain.notification.NotificationReq;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notification")
public class AdminNotificationController implements AdminNotificationApi {

    private final AdminNotificationService adminNotificationService;

    @Override
    @PostMapping
    public ResponseEntity<?> createNotification(
            @Valid @RequestBody NotificationReq notificationReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails){
        adminNotificationService.createNotification(notificationReq, userDetails.getId());
        return ResponseEntity.ok(SuccessRes.from("공지사항 생성에 성공했습니다."));
    }

    @Override
    @PutMapping("/{notificationId}")
    public ResponseEntity<?> updateNotification(
            @PathVariable(value = "notificationId") Long notificationId,
            @Valid @RequestBody NotificationReq notificationReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails){
        adminNotificationService.updateNotification(notificationReq, notificationId, userDetails.getId());
        return ResponseEntity.ok(SuccessRes.from("공지사항 수정에 성공했습니다."));
    }

    @Override
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable(value = "notificationId") Long notificationId,
            @AuthenticationPrincipal SecurityUserDetails userDetails){
        adminNotificationService.deleteNotification(notificationId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}