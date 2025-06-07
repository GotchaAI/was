package Gotcha.domain.report.repository;

import gotcha_domain.report.UserReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    @Query("""
                SELECT ur
                FROM UserReport ur
                JOIN ur.user u
                WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(u.nickname) LIKE LOWER(CONCAT(:keyword, '%')))
            """)
    Page<UserReport> findAllByNickname(@Param("keyword") String keyword, Pageable pageable);
}
