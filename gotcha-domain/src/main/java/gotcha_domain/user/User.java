package gotcha_domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.achivement.UserAchievement;
import gotcha_domain.friend.Friend;
import gotcha_domain.friend.FriendRequest;
import gotcha_domain.gamehistory.UserGameHistory;
import gotcha_domain.inquiry.Answer;
import gotcha_domain.inquiry.Inquiry;
import gotcha_domain.notification.Notification;
import gotcha_domain.report.BugReport;
import gotcha_domain.report.UserReport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Integer warningCount;

    @Setter
    private LocalDateTime lastLogout;

    private Boolean isLocked;

    @Setter
    private int level;

    @Setter
    private long exp;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_option", nullable = false)
    private ChatOption chatOption = ChatOption.ALLOW_ALL;

    @Enumerated(EnumType.STRING)
    @Column(name = "private_chat_option", nullable = false)
    private PrivateChatOption privateChatOption = PrivateChatOption.ALLOW;

    @JsonIgnore
    @OneToMany(mappedBy = "player")
    private Set<UserGameHistory> userGameHistories = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer")
    private Set<Answer> answers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer")
    private Set<Inquiry> inquiries = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer")
    private Set<Notification> notifications = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<UserReport> userReports = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<UserAchievement> userAchievements = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user1")
    private Set<Friend> friends = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user2")
    private Set<Friend> friendOf = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fromUser")
    private Set<FriendRequest> sentFriendRequests = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "toUser")
    private Set<FriendRequest> receivedFriendRequests = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<BugReport> bugReports = new HashSet<>();

    @Builder
    public User(Long id, String email, String password, String nickname, Role role, String uuid) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.uuid = uuid;
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateChatSettings(ChatOption chatOption, PrivateChatOption privateChatOption) {
        this.chatOption = chatOption;
        this.privateChatOption = privateChatOption;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;

        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }
}
