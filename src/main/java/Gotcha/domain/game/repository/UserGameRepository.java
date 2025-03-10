package Gotcha.domain.game.repository;

import Gotcha.domain.game.entity.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGameRepository extends JpaRepository<UserGame, Long> {
}
