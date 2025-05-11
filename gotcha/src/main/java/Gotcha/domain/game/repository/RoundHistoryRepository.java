package Gotcha.domain.game.repository;

import gotcha_domain.game.RoundHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundHistoryRepository extends JpaRepository<RoundHistory, Long> {

}
