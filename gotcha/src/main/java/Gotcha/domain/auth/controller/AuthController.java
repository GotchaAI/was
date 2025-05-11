package Gotcha.domain.auth.controller;

import Gotcha.domain.auth.api.AuthApi;
import Gotcha.domain.auth.dto.EmailReq;
import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import Gotcha.domain.auth.service.AuthService;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_auth.dto.TokenDto;
import gotcha_auth.exception.JwtExceptionCode;
import gotcha_common.dto.EmailCodeVerifyReq;
import gotcha_common.dto.SuccessRes;
import gotcha_common.exception.CustomException;
import gotcha_common.mail.MailCodeService;
import gotcha_common.util.CookieUtil;
import gotcha_user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static gotcha_auth.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static gotcha_auth.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final MailCodeService mailCodeService;
    private final UserService userService;
//    private final CookieCsrfTokenRepository csrfTokenRepository;


    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpReq signUpReq) {
        TokenDto tokenDto = authService.signUp(signUpReq);

        return createTokenRes(tokenDto, tokenDto.autoSignIn());
    }

    @PostMapping("/guest/sign-up")
    public ResponseEntity<?> guestSignUp(@Valid @RequestBody SignUpReq signUpReq,
                                    @AuthenticationPrincipal SecurityUserDetails userDetails) {
        TokenDto tokenDto = authService.guestSignUp(signUpReq, userDetails);

        return createTokenRes(tokenDto, tokenDto.autoSignIn());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInReq signInReq) {
        TokenDto tokenDto = authService.signIn(signInReq);

        return createTokenRes(tokenDto, signInReq.autoSignIn());
    }

    @PostMapping("/guest/sign-in")
    public ResponseEntity<?> guestSignIn(){
        TokenDto tokenDto = authService.guestSignIn();

        return createTokenRes(tokenDto, tokenDto.autoSignIn());
    }

    @PostMapping("/token-reissue")
    public ResponseEntity<?> reIssueToken(@CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }
        TokenDto tokenDto = authService.reissueAccessToken(refreshToken);

        return createTokenRes(tokenDto, tokenDto.autoSignIn());
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

//    @GetMapping("/csrf-token")
//    public ResponseEntity<Void> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
//        CsrfToken token = csrfTokenRepository.generateToken(request);
//        csrfTokenRepository.saveToken(token, request, response);
//        return ResponseEntity.ok().build();
//    }

    private ResponseEntity<?> createTokenRes(TokenDto tokenDto, boolean autoSignIn) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenDto.accessToken());
        responseData.put("expiredAt", tokenDto.accessTokenExpiredAt());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        cookieUtil.createCookie(REFRESH_COOKIE_VALUE,
                                tokenDto.refreshToken(), autoSignIn).toString())
                .body(responseData);
    }
}

