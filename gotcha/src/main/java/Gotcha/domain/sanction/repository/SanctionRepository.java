package Gotcha.domain.sanction.repository;

import gotcha_domain.sanction.SanctionType;
import gotcha_domain.sanction.UserSanction;
import gotcha_domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SanctionRepository extends JpaRepository<UserSanction, Long> {
    Optional<UserSanction> findTopByUserAndIsReadIsFalseOrderByCreatedAtDesc(User user);
    Optional<UserSanction> findTopByUserAndSanctionTypeAndIsReadIsFalseOrderByCreatedAtDesc(User user, SanctionType type);
}
