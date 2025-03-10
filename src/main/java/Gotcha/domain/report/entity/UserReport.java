package Gotcha.domain.report.entity;

import Gotcha.common.converter.StringListConverter;
import Gotcha.common.entity.BaseTimeEntity;
import Gotcha.domain.user.entity.User;
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
public class UserReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private UserReportType userReportType;

    @NotNull
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = StringListConverter.class)
    @Column(name = "chat_log", length = 500)
    private List<String> chatLog;

    @Builder
    public UserReport(UserReportType userReportType, String detail, List<String> chatLog){
        this.userReportType = userReportType;
        this.detail = detail;
        this.chatLog = chatLog;
    }
}
