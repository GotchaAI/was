package Gotcha.domain.report.controller;

import Gotcha.domain.report.dto.UserReportReq;
import Gotcha.domain.report.service.UserReportService;
import gotcha_common.dto.SuccessRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report/user")
public class UserReportController {
    private final UserReportService userReportService;

    @PostMapping()
    public ResponseEntity<?> reportUser(@RequestBody @Valid UserReportReq reportReq) {
        userReportService.reportUser(reportReq);
        return ResponseEntity.ok(SuccessRes.from("성공적으로 신고하였습니다."));
    }
}
