package gotcha_ranking.controller;

import gotcha_ranking.api.RankingApi;
import gotcha_ranking.service.RankingRedisService;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Override
    @GetMapping("/mine")
    public ResponseEntity<?> getMyRanking(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(rankingRedisService.getUserRank(userDetails.getId()));
    }
}
