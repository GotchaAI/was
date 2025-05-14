package Gotcha.domain.mypage.controller;

import Gotcha.domain.mypage.service.MypageService;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MypageController {
    private final MypageService mypageService;

    @GetMapping("/games")
    public ResponseEntity<?> getUserGameHistories(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(mypageService.getUserGameHistories(userDetails.getId()));
    }
}
