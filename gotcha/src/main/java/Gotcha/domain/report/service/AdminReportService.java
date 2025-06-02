package Gotcha.domain.report.service;

import Gotcha.domain.report.dto.UserReportRes;
import Gotcha.domain.report.repository.UserReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminReportService {
    private final UserReportRepository userReportRepository;

    private final Integer REPORTS_PER_PAGE = 10;

    public Page<UserReportRes> getUserReports(String keyword, Integer page) {
        Pageable pageable = PageRequest.of(page, REPORTS_PER_PAGE);

        return userReportRepository
                .findAllByNickname(keyword, pageable)
                .map(UserReportRes::from);
    }
}
