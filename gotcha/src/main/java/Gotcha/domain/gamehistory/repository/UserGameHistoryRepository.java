package Gotcha.domain.gamehistory.repository;

import gotcha_domain.gamehistory.UserGameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserGameHistoryRepository extends JpaRepository<UserGameHistory, Long> {
    @Query("SELECT ug FROM UserGameHistory ug " +
            "JOIN FETCH ug.gameHistory g " +
            "WHERE ug.player.id = :userId " +
            "ORDER BY g.createdAt DESC")
    List<UserGameHistory> findAllByUserIdWithGameHistory(Long userId);

    @Query("SELECT ug FROM UserGameHistory ug " +
            "JOIN FETCH ug.gameHistory g " +
            "WHERE ug.player.id = :userId and ug.gameHistory.id = :gameId ")
    Optional<UserGameHistory> findByUserIdAndGameHistoryId(Long userId, Long gameId);
}
