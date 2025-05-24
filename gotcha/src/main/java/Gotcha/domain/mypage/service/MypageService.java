package Gotcha.domain.mypage.service;

import Gotcha.domain.gamehistory.dto.UserGameHistoryDetailRes;
import Gotcha.domain.gamehistory.dto.UserGameHistorySummaryRes;
import Gotcha.domain.gamehistory.service.GameHistoryService;
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

    public List<UserGameHistorySummaryRes> getUserGameSummaries(Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameHistoryService.getUserGameHistories(userId);
    }

    public UserGameHistoryDetailRes getUserGameDetail(Long gameId, Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameHistoryService.getUserGameDetail(gameId, userId);
    }
}
