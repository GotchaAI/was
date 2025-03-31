package Gotcha.domain.notification.entity;

import Gotcha.common.entity.BaseTimeEntity;
import Gotcha.domain.notification.dto.NotificationReq;
import Gotcha.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private Boolean isFixed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Builder
    public Notification(String title, String content, User writer, Boolean isFixed){
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.isFixed = isFixed;
    }

    public void update(NotificationReq req){
        this.title = req.title();
        this.content = req.content();
        this.isFixed = Boolean.TRUE;
    }
}
