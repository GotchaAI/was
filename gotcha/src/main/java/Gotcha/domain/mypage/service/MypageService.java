package Gotcha.domain.mypage.service;

import Gotcha.domain.gamehistory.dto.UserGameHistoryDetailRes;
import Gotcha.domain.gamehistory.dto.UserGameHistorySummaryRes;
import Gotcha.domain.gamehistory.service.GameHistoryService;
import gotcha_common.util.RedisUtil;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserService userService;
    private final GameHistoryService gameHistoryService;
    private final RedisUtil redisUtil;

    public List<UserGameHistorySummaryRes> getUserGameSummaries(Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameHistoryService.getUserGameHistories(userId);
    }

    public UserGameHistoryDetailRes getUserGameDetail(Long gameId, Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameHistoryService.getUserGameDetail(gameId, userId);
    }

    public void modifyUserNickname(Long userId, String nickname) {
        userService.changeNickname(userId, nickname);
    }
}
