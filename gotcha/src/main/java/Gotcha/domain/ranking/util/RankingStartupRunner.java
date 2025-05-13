package Gotcha.domain.ranking.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingStartupRunner implements ApplicationRunner {

    private final RankingInitializer rankingInitializer;

    @Override
    public void run(ApplicationArguments args) {
        rankingInitializer.initializeRanking();
    }
}

