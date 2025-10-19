package Gotcha.domain.mypage.service;

import Gotcha.domain.mypage.dto.ChatSettingReq;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import socket_server.gamehistory.dto.UserGameHistoryDetailRes;
import socket_server.gamehistory.dto.UserGameHistorySummaryRes;
import socket_server.gamehistory.service.GameHistoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserService userService;
    private final GameHistoryService gameHistoryService;

    @Transactional(readOnly = true)
    public List<UserGameHistorySummaryRes> getUserGameSummaries(Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameHistoryService.getUserGameHistories(userId);
    }

    @Transactional(readOnly = true)
    public UserGameHistoryDetailRes getUserGameDetail(Long gameId, Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameHistoryService.getUserGameDetail(gameId, userId);
    }

    @Transactional
    public void modifyUserNickname(Long userId, String nickname) {
        userService.changeNickname(userId, nickname);
    }

    @Transactional
    public void modifyUserChatSetting(Long userId, ChatSettingReq chatSettingReq) {
        userService.updateUserChatSetting(userId, chatSettingReq.chatOption(), chatSettingReq.privateChatOption());
    }
}
