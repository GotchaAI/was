package Gotcha.domain.achivement.repository;

import gotcha_domain.achivement.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
}
