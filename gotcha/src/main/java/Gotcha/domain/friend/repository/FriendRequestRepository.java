package Gotcha.domain.friend.repository;

import gotcha_domain.friend.FriendRequest;
import gotcha_domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.fromUser WHERE fr.toUser.id = :userId")
    List<FriendRequest> findWithFromUserByToUserId(@Param("userId") Long userId);

    @Query("""
                SELECT COUNT(fr) > 0 
                FROM FriendRequest fr 
                WHERE 
                    (fr.fromUser = :user1 AND fr.toUser = :user2) OR 
                    (fr.fromUser = :user2 AND fr.toUser = :user1)
            """)
    boolean existsMutualRequest(@Param("user1") User user1, @Param("user2") User user2);
}
