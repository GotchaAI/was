package Gotcha.domain.sanction.repository;

import gotcha_domain.sanction.UserSanction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SanctionRepository extends JpaRepository<UserSanction, Long> {
}
