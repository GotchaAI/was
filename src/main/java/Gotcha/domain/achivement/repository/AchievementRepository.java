package Gotcha.domain.achivement.repository;

import Gotcha.domain.achivement.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
