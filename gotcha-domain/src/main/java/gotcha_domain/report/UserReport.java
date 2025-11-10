package gotcha_domain.report;

import gotcha_common.entity.BaseTimeEntity;
import gotcha_domain.chat.ChatMessage;
import gotcha_domain.chat.converter.ChatMessageListConverter;
import gotcha_domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private UserReportType userReportType;

    @NotNull
    private String detail; // by user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = ChatMessageListConverter.class)
    @Column(name = "chat_log", columnDefinition = "LONGTEXT")
    private List<ChatMessage> chatLog;

    @Builder
    public UserReport(UserReportType userReportType, String detail, List<ChatMessage> chatLog, User user){
        this.userReportType = userReportType;
        this.detail = detail;
        this.chatLog = chatLog;
        this.user = user;
    }

    public static UserReport of(UserReportType userReportType, String detail, List<ChatMessage> chatLog, User user) {
        return new UserReport(userReportType, detail, chatLog, user);
    }

}
