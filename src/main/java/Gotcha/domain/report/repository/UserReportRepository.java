package Gotcha.domain.report.repository;

import Gotcha.domain.report.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
