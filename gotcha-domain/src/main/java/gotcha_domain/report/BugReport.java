package gotcha_domain.report;

import gotcha_common.converter.StringListConverter;
import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.gamehistory.GameHistory;
import gotcha_domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BugReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private BugReportType bugReportType;

    @NotNull
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = StringListConverter.class)
    @Column(name = "chat_log", length = 500)
    private List<String> chatLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameHistory gameHistory;

    @Builder
    public BugReport(BugReportType bugReportType, String detail, List<String> chatLog) {
        this.bugReportType = bugReportType;
        this.detail = detail;
        this.chatLog = chatLog;
    }
}
