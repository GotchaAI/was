package gotcha_domain.friend;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Friend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    public Friend(User userA, User userB) {
        if (userA.getId() < userB.getId()) {
            this.user1 = userA;
            this.user2 = userB;
        } else {
            this.user1 = userB;
            this.user2 = userA;
        }
    }

    public boolean contains(User user) {
        return user1.equals(user) || user2.equals(user);
    }

    public User getOther(User me) {
        return user1.equals(me) ? user2 : user1;
    }
}
