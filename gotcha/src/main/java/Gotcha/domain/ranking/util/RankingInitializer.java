package Gotcha.domain.ranking.util;

import Gotcha.domain.ranking.service.RankingRedisService;
import gotcha_domain.user.User;
import gotcha_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingInitializer {

    private final UserRepository userRepository;
    private final RankingRedisService rankingRedisService;

    public void initializeRanking() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            rankingRedisService.updateUserExpRanking(user.getId(), user.getExp());
        }
    }
}
