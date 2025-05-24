package Gotcha.domain.ranking.controller;

import Gotcha.domain.ranking.api.RankingApi;
import Gotcha.domain.ranking.service.RankingRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingController implements RankingApi {
    private final RankingRedisService rankingRedisService;

    @Override
    @GetMapping()
    public ResponseEntity<?> getUserRankingPage(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(rankingRedisService.getUserRankingPage(page));
    }
}
