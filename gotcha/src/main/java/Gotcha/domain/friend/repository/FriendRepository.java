package Gotcha.domain.friend.repository;

import gotcha_domain.friend.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("""
                SELECT f FROM Friend f
                WHERE f.user1.id = :userId OR f.user2.id = :userId
            """)
    List<Friend> findAllByUserId(@Param("userId") Long userId);

    @Query("""
                SELECT f FROM Friend f
                WHERE (f.user1.id = :userId AND LOWER(f.user2.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
                   OR (f.user2.id = :userId AND LOWER(f.user1.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    List<Friend> searchFriendsByNickname(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Query("""
                SELECT COUNT(f) > 0 FROM Friend f
                WHERE (f.user1.id = :id1 AND f.user2.id = :id2)
                   OR (f.user1.id = :id2 AND f.user2.id = :id1)
            """)
    boolean existsFriendRelationBetween(@Param("id1") Long id1, @Param("id2") Long id2);

    @Query("""
                SELECT f FROM Friend f
                WHERE (f.user1.id = :id1 AND f.user2.id = :id2)
                   OR (f.user1.id = :id2 AND f.user2.id = :id1)
            """)
    Friend findFriendRelation(@Param("id1") Long id1, @Param("id2") Long id2);
}
