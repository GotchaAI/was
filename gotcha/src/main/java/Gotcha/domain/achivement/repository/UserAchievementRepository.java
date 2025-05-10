package Gotcha.domain.achivement.repository;

import Gotcha.domain.achivement.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
}
