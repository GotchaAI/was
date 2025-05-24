package Gotcha.domain.gamehistory.repository;

import gotcha_domain.gamehistory.RoundHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundHistoryRepository extends JpaRepository<RoundHistory, Long> {

}