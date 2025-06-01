package Gotcha.domain.report.controller;

import Gotcha.domain.report.api.AdminReportApi;
import Gotcha.domain.report.service.AdminReportService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/report")
public class AdminReportController implements AdminReportApi {
    private final AdminReportService adminReportService;

    @GetMapping("/users")
    public ResponseEntity<?> getUserReports(@RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page) {
        return ResponseEntity.ok(adminReportService.getUserReports(keyword, page));
    }

}
