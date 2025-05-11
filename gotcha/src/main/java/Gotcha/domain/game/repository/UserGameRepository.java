package Gotcha.domain.game.repository;

import gotcha_domain.game.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {
}
