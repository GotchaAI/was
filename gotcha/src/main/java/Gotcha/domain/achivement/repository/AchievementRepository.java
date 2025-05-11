package Gotcha.domain.achivement.repository;

import gotcha_domain.achivement.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
