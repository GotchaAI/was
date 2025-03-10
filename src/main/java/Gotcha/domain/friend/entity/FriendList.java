package Gotcha.domain.friend.entity;

import Gotcha.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "friendList", fetch = FetchType.LAZY)
    private List<Friend> friends;

    @Builder
    public FriendList(User user) {
        this.user = user;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
        friend.setFriendList(this);
    }

    public void removeFriend(Friend friend) {
        this.friends.remove(friend);
        friend.setFriendList(null);
    }
}
