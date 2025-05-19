package gotcha_user.repository;

import gotcha_domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String uuid);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByUuid(String uuid);

    List<User> findAll();
}
