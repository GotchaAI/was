package Gotcha.domain.game.repository;

import Gotcha.domain.game.entity.RoundHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundHistoryRepository extends JpaRepository<RoundHistory, Long> {

}
