package Gotcha.domain.user.entity;

import Gotcha.common.entity.BaseTimeEntity;
import Gotcha.domain.achivement.entity.UserAchievement;
import Gotcha.domain.friend.entity.Friend;
import Gotcha.domain.friend.entity.FriendRequest;
import Gotcha.domain.game.entity.UserGame;
import Gotcha.domain.inquiry.entity.Answer;
import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.notification.entity.Notification;
import Gotcha.domain.report.entity.BugReport;
import Gotcha.domain.report.entity.UserReport;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private LocalDateTime lastLogout;

    private Boolean isLocked;

    @JsonIgnore
    @OneToMany(mappedBy = "player")
    private List<UserGame> userGames = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer")
    private List<Answer> answers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer")
    private List<Inquiry> inquiries = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "writer")
    private List<Notification> notifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<UserReport> userReports = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<UserAchievement> userAchievements = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fromUser")
    private List<FriendRequest> sentFriendRequests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "toUser")
    private List<FriendRequest> receivedFriendRequests = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Friend> friends = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "friend")
    private List<Friend> friendOf = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BugReport> bugReports = new ArrayList<>();

    @Builder
    public User(Long id, String email, String password, String nickname, Role role){
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }
}

