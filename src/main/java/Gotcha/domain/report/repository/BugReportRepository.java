package Gotcha.domain.report.repository;

import Gotcha.domain.report.entity.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugReportRepository extends JpaRepository<BugReport, Long> {
}
