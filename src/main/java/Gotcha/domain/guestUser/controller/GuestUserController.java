package Gotcha.domain.guestUser.controller;

import Gotcha.common.util.CookieUtil;
import Gotcha.domain.auth.dto.TokenDto;
import Gotcha.domain.guestUser.service.GuestUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static Gotcha.common.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/guest")
public class GuestUserController {
    private final GuestUserService guestUserService;
    private final CookieUtil cookieUtil;

    @PostMapping("/sign-in")
    public ResponseEntity<?> guestSignIn() {
        TokenDto tokenDto = guestUserService.createGuestAccessToken();

        return createTokenRes(tokenDto);
    }

    private ResponseEntity<?> createTokenRes(TokenDto tokenDto) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenDto.accessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        cookieUtil.createCookie(REFRESH_COOKIE_VALUE,
                                tokenDto.refreshToken()).toString())
                .body(responseData);
    }
}
