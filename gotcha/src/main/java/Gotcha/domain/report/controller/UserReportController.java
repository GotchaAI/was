package Gotcha.domain.report.controller;

import Gotcha.domain.report.api.UserReportApi;
import Gotcha.domain.report.dto.UserReportReq;
import Gotcha.domain.report.service.UserReportService;
import gotcha_common.dto.SuccessRes;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report/user")
public class UserReportController implements UserReportApi {
    private final UserReportService userReportService;

    @PostMapping()
    public ResponseEntity<?> reportUser(@RequestBody @Valid UserReportReq reportReq,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails) {
        userReportService.reportUser(reportReq, userDetails.getUuid());
        return ResponseEntity.ok(SuccessRes.from("성공적으로 신고하였습니다."));
    }
}
