package Gotcha.domain.report.repository;

import gotcha_domain.report.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
