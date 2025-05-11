package Gotcha.domain.report.repository;

import gotcha_domain.report.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugReportRepository extends JpaRepository<BugReport, Long> {
}
