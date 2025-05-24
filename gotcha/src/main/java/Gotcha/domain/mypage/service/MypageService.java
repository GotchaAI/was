package Gotcha.domain.mypage.service;

import Gotcha.domain.game.dto.UserGameHistoryDetailRes;
import Gotcha.domain.game.dto.UserGameHistorySummaryRes;
import Gotcha.domain.game.service.GameService;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserService userService;
    private final GameService gameService;

    public List<UserGameHistorySummaryRes> getUserGameSummaries(Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameService.getUserGameHistories(userId);
    }

    public UserGameHistoryDetailRes getUserGameDetail(Long gameId, Long userId) {
        User user = userService.findUserByUserId(userId);

        return gameService.getUserGameDetail(gameId, userId);
    }
}
