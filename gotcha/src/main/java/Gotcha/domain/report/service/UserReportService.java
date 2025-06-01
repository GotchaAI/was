package Gotcha.domain.report.service;

import Gotcha.domain.report.dto.UserReportReq;
import Gotcha.domain.report.repository.UserReportRepository;
import gotcha_domain.chat.ChatMessage;
import gotcha_domain.report.UserReport;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.domain.chat.service.ChatLogService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReportService {
    private final UserReportRepository userReportRepository;
    private final ChatLogService chatLogService;
    private final UserService userService;

    public void reportUser(UserReportReq reportReq) {
        List<ChatMessage> chatLog = chatLogService.getSurroundingMessages(reportReq.chatType(), reportReq.identifier(), reportReq.reportedUuId(), reportReq.chatTime(), 10);
        User reportedUser = userService.findUserByUuid(reportReq.reportedUuId());

        UserReport userReport = UserReport.of(
                reportReq.reportType(),
                reportReq.reason(),
                chatLog,
                reportedUser
        );

        userReportRepository.save(userReport);
    }

}
