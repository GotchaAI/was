package socket_server.gamehistory.service;

import gotcha_domain.gamehistory.GameHistory;
import gotcha_domain.user.User;
import org.springframework.transaction.annotation.Transactional;
import socket_server.domain.game.model.Game;
import socket_server.gamehistory.dto.UserGameHistoryDetailRes;
import socket_server.gamehistory.dto.UserGameHistorySummaryRes;
import socket_server.gamehistory.exception.GameHistoryExceptionCode;
import socket_server.gamehistory.repository.GameHistoryRepository;
import socket_server.gamehistory.repository.UserGameHistoryRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.gamehistory.UserGameHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
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



    @Transactional
    public List<UserGameHistory> createGameHistoryWithUsers(Game game, List<User> users) {
        // 게임 히스토리 생성
        GameHistory gameHistory = GameHistory.builder()
                .gameType(game.getGameType())
                .difficulty(game.getDifficulty())
                .totalRounds(game.getTotalRounds())
                .playerWon(game.getPlayerWon())
                .aiScore(game.getAiScore())
                .playerScore(game.getPlayerScore())
                .build();

        // DB 저장
        GameHistory savedGameHistory = gameHistoryRepository.save(gameHistory);

        // 모든 사용자와 게임 히스토리 매핑 (필수)
        List<UserGameHistory> userGameHistories = users.stream()
                .map(user -> UserGameHistory.builder()
                        .player(user)
                        .gameHistory(savedGameHistory)
                        .build())
                .toList();

        return userGameHistoryRepository.saveAll(userGameHistories);
    }




}
