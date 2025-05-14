package Gotcha.domain.game.service;

import Gotcha.domain.game.dto.UserGameHistoriesRes;
import Gotcha.domain.game.repository.UserGameRepository;
import gotcha_domain.game.UserGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final UserGameRepository userGameRepository;

    public List<UserGameHistoriesRes> getUserGameHistories(Long userId) {
        List<UserGame> userGames = userGameRepository.findAllByUserIdWithGame(userId);

        return userGames.stream().map(UserGameHistoriesRes::from).collect(Collectors.toList());
    }
}
