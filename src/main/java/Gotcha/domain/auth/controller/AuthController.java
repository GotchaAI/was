package Gotcha.domain.auth.controller;

import Gotcha.common.api.SuccessRes;
import Gotcha.common.exception.CustomException;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.mail.MailCodeService;
import Gotcha.common.util.CookieUtil;
import Gotcha.domain.auth.api.AuthApi;
import Gotcha.domain.auth.dto.EmailCodeVerifyReq;
import Gotcha.domain.auth.dto.EmailReq;
import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import Gotcha.domain.auth.dto.TokenDto;
import Gotcha.domain.auth.service.AuthService;
import Gotcha.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static Gotcha.common.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static Gotcha.common.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final MailCodeService mailCodeService;
    private final UserService userService;

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

    @PostMapping("/guest/sign-in")
    public ResponseEntity<?> guestSignIn(){
        TokenDto tokenDto = authService.guestSignIn();

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

    @PostMapping("/email/send")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailReq emailReq) {
        userService.checkEmail(emailReq.email());
        mailCodeService.sendCodeToMail(emailReq.email());
        return ResponseEntity.ok(SuccessRes.from("인증 코드가 발송되었습니다."));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailCodeVerifyReq emailCodeVerifyReq) {
        mailCodeService.verifiedCode(emailCodeVerifyReq);
        return ResponseEntity.ok(SuccessRes.from("이메일이 인증되었습니다."));
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> signOut(@RequestHeader(value = ACCESS_HEADER_VALUE, required = false) String accessToken,
                                     @CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken,
                                     HttpServletResponse response) {

        //Todo : Https 배포 후에는 accessToken, refreshToken null인지 검증과정 필요, 현재는 http라서 required = false 처리해둠

        authService.signOut(accessToken, refreshToken, response);

        return ResponseEntity.ok(SuccessRes.from("로그아웃 되었습니다."));
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

