package Gotcha.domain.gamehistory.service;

import Gotcha.domain.gamehistory.dto.UserGameHistoryDetailRes;
import Gotcha.domain.gamehistory.dto.UserGameHistorySummaryRes;
import Gotcha.domain.gamehistory.exception.GameHistoryExceptionCode;
import Gotcha.domain.gamehistory.repository.UserGameHistoryRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.gamehistory.UserGameHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameHistoryService {
    private final UserGameHistoryRepository userGameHistoryRepository;

    public List<UserGameHistorySummaryRes> getUserGameHistories(Long userId) {
        List<UserGameHistory> userGameHistories = userGameHistoryRepository.findAllByUserIdWithGameHistory(userId);

        return userGameHistories.stream().map(UserGameHistorySummaryRes::from).collect(Collectors.toList());
    }

    public UserGameHistoryDetailRes getUserGameDetail(Long gameId, Long userId) {
        UserGameHistory userGameHistory = userGameHistoryRepository.findByUserIdAndGameHistoryId(userId, gameId)
                .orElseThrow(() -> new CustomException(GameHistoryExceptionCode.GAME_HISTORY_NOT_FOUND));

        return UserGameHistoryDetailRes.from(userGameHistory);
    }
}
