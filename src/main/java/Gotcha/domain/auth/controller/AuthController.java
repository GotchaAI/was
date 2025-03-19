package Gotcha.domain.auth.controller;

import Gotcha.common.exception.CustomException;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.util.CookieUtil;
import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import Gotcha.domain.auth.dto.TokenDto;
import Gotcha.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static Gotcha.common.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpReq signUpReq) {
        TokenDto tokenDto = authService.signUp(signUpReq);

        return createTokenRes(tokenDto);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInReq signInReq) {
        TokenDto tokenDto = authService.signIn(signInReq);

        return createTokenRes(tokenDto);
    }

    @PostMapping("/token-reissue")
    public ResponseEntity<?> reIssueToken(@CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }
        TokenDto tokenDto = authService.reissueAccessToken(refreshToken);

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

