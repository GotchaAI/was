package gotcha_ranking.util;

import gotcha_ranking.service.RankingRedisService;
import gotcha_domain.user.User;
import gotcha_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingInitializer {

    private final UserRepository userRepository;
    private final RankingRedisService rankingRedisService;
    private static final int PAGE_SIZE = 100; // 한 번에 처리할 사용자 수

    public void initializeRanking() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        Page<User> userPage;

        do {
            userPage = userRepository.findAll(pageable);
            for (User user : userPage.getContent()) {
                rankingRedisService.updateUserExpRanking(user.getId(), user.getExp());
            }

            pageable = userPage.nextPageable();
        } while (userPage.hasNext());
    }
}
