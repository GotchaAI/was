package gotcha_domain.sanction;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.report.UserReport;
import gotcha_domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSanction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제재를 받은 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 조치를 내린 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "sanction_type", nullable = false)
    private SanctionType sanctionType;

    // 제재 사유
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    // 제재 만료 일시
    @Column(name = "expiry_date")
    private LocalDateTime expiresAt;

    // 근거가 된 신고(nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_report_id")
    private UserReport userReport;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Builder
    public UserSanction(User user, User admin, SanctionType sanctionType, String reason, LocalDateTime expiresAt, UserReport userReport) {
        this.user = user;
        this.admin = admin;
        this.sanctionType = sanctionType;
        this.reason = reason;
        this.expiresAt = expiresAt;
        this.userReport = userReport;
    }

    public void markAsRead() {
        this.isRead = true;
    }


}
