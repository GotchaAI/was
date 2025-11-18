package gotcha_user.repository;

import gotcha_domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUuid(String uuid);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends LEFT JOIN FETCH u.friendOf WHERE u.uuid = :uuid")
    Optional<User> findByUuidWithFriends(@Param("uuid") String uuid);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends LEFT JOIN FETCH u.friendOf WHERE u.nickname = :nickname")
    Optional<User> findByNicknameWithFriends(@Param("nickname") String nickname);

    Optional<User> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByUuid(String uuid);

    List<User> findAll();

    List<User> findByNicknameContaining(String keyword);
}
