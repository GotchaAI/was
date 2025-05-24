package Gotcha.domain.gamehistory.repository;

import gotcha_domain.gamehistory.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameHistoryRepository extends JpaRepository<GameHistory,Long> {
}
