package Gotcha.domain.friend.repository;

import Gotcha.domain.friend.entity.FriendList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendListRepository extends JpaRepository<FriendList, Long> {
}
