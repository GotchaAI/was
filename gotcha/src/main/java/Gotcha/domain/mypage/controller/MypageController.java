package Gotcha.domain.mypage.controller;

import Gotcha.domain.mypage.api.MypageApi;
import Gotcha.domain.mypage.service.MypageService;
import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MypageController implements MypageApi {
    private final MypageService mypageService;

    @Operation
    @GetMapping("/games")
    public ResponseEntity<?> getUserGameSummaries(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(mypageService.getUserGameSummaries(userDetails.getId()));
    }

    @Operation
    @GetMapping("/games/{id}")
    public ResponseEntity<?> getUserGameDetails(@PathVariable(value = "id") Long gameId,
                                                @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(mypageService.getUserGameDetail(gameId, userDetails.getId()));
    }
}
