package Gotcha.domain.user.controller;

import Gotcha.common.api.SuccessRes;
import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.user.api.UserApi;
import Gotcha.domain.user.dto.NicknameReq;
import Gotcha.domain.user.dto.UserInfoRes;
import Gotcha.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    @PostMapping("/nickname-check")
    public ResponseEntity<?> checkNickname(@Valid @RequestBody NicknameReq nicknameReq) {
        userService.checkNickname(nicknameReq.nickname());

        return ResponseEntity.ok(SuccessRes.from("사용 가능한 닉네임입니다."));
    }

    @GetMapping("/me/main-info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal SecurityUserDetails userDetails){
        UserInfoRes userInfoRes = userService.getUserInfo(userDetails.getId(), userDetails.getRole());

        return ResponseEntity.status(HttpStatus.OK).body(userInfoRes);
    }
}
