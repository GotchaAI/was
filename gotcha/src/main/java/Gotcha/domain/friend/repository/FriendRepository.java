package Gotcha.domain.friend.repository;

import gotcha_domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
