package Gotcha.domain.game.service;

import Gotcha.domain.game.dto.UserGameHistoryDetailRes;
import Gotcha.domain.game.dto.UserGameHistorySummaryRes;
import Gotcha.domain.game.exception.GameExceptionCode;
import Gotcha.domain.game.repository.UserGameRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.game.UserGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final UserGameRepository userGameRepository;

    public List<UserGameHistorySummaryRes> getUserGameHistories(Long userId) {
        List<UserGame> userGames = userGameRepository.findAllByUserIdWithGame(userId);

        return userGames.stream().map(UserGameHistorySummaryRes::from).collect(Collectors.toList());
    }

    public UserGameHistoryDetailRes getUserGameDetail(Long gameId, Long userId) {
        UserGame userGame = userGameRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new CustomException(GameExceptionCode.GAME_NOT_FOUND));

        return UserGameHistoryDetailRes.from(userGame);
    }
}
