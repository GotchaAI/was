package Gotcha.domain.report.dto;

import gotcha_domain.chat.ChatMessage;
import gotcha_domain.report.UserReport;
import gotcha_domain.report.UserReportType;

import java.time.LocalDateTime;
import java.util.List;

public record UserReportRes (
        Long userReportId,
        String reportedUserUuid,
        LocalDateTime reportedAt,
        String nickname,
        UserReportType reportType,
        String detail,
        List<ChatMessage> chatLog
) {
    public static UserReportRes from(UserReport userReport) {
        return new UserReportRes(
                userReport.getId(),
                userReport.getUser().getUuid(),
                userReport.getCreatedAt(),
                userReport.getUser().getNickname(),
                userReport.getUserReportType(),
                userReport.getDetail(),
                userReport.getChatLog()
        );
    }
}
