package Gotcha.domain.report.service;

import Gotcha.domain.report.dto.UserReportReq;
import Gotcha.domain.report.exception.ReportExceptionCode;
import Gotcha.domain.report.repository.UserReportRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
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

    public void reportUser(UserReportReq reportReq, SecurityUserDetails userDetails) {
        if (reportReq.reportedUserNickname().equals(userDetails.getNickname())) {
            throw new CustomException(ReportExceptionCode.CANNOT_REPORT_SELF);
        }

        List<ChatMessage> chatLog = chatLogService.getSurroundingMessages(reportReq.chatType(), reportReq.identifier(), userDetails.getUuid(), reportReq.reportedChatTime(), 10);
        User reportedUser = userService.findUserByNickname(reportReq.reportedUserNickname());

        UserReport userReport = UserReport.of(
                reportReq.reportType(),
                reportReq.detail(),
                chatLog,
                reportedUser
        );

        userReportRepository.save(userReport);
    }

    public int countReportByUserId(Long userId) {
        return userReportRepository.countByUser_Id(userId);
    }

    public UserReport findUserReportById(Long reportId){
        return userReportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ReportExceptionCode.REPORT_NOT_FOUND));
    }

}
