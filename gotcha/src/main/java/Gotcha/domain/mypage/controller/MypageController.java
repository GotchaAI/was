package Gotcha.domain.mypage.controller;

import Gotcha.domain.mypage.api.MypageApi;
import Gotcha.domain.mypage.service.MypageService;
import gotcha_common.dto.SuccessRes;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_user.dto.NicknameReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MypageController implements MypageApi {
    private final MypageService mypageService;

    @GetMapping("/games")
    public ResponseEntity<?> getUserGameSummaries(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(mypageService.getUserGameSummaries(userDetails.getId()));
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<?> getUserGameDetails(@PathVariable(value = "id") Long gameId,
                                                @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(mypageService.getUserGameDetail(gameId, userDetails.getId()));
    }

    @PutMapping("/nickname")
    public ResponseEntity<?> modifyUserNickname(@Valid @RequestBody NicknameReq nicknameReq,
                                                @AuthenticationPrincipal SecurityUserDetails userDetails) {
        mypageService.modifyUserNickname(userDetails.getId(), nicknameReq.nickname());
        return ResponseEntity.ok(SuccessRes.from("성공적으로 수정되었습니다."));
    }
}