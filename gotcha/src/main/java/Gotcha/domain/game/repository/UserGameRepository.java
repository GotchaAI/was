package Gotcha.domain.game.repository;

import gotcha_domain.game.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {
    @Query("SELECT ug FROM UserGame ug " +
            "JOIN FETCH ug.game g " +
            "WHERE ug.player.id = :userId " +
            "ORDER BY g.createdAt DESC")
    List<UserGame> findAllByUserIdWithGame(Long userId);

    @Query("SELECT ug FROM UserGame ug " +
            "JOIN FETCH ug.game g " +
            "WHERE ug.player.id = :userId and ug.game.id = :gameId " +
            "ORDER BY g.createdAt DESC")
    Optional<UserGame> findByUserIdAndGameId(Long userId, Long gameId);
}
