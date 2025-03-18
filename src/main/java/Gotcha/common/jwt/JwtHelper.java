package Gotcha.common.jwt;

import Gotcha.common.exception.CustomException;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.util.CookieUtil;
import Gotcha.domain.auth.dto.TokenDto;
import Gotcha.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static Gotcha.common.jwt.JwtProperties.REFRESH_COOKIE_VALUE;
import static Gotcha.common.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@Component
public class JwtHelper {

    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final BlackListTokenService blackListTokenService;

    public TokenDto createToken(User user) {
        Long userId = user.getId();
        String email = user.getEmail();
        String role = String.valueOf(user.getRole());

        String accessToken = TOKEN_PREFIX + tokenProvider.createAccessToken(role, userId, email);
        String refreshToken = tokenProvider.createRefreshToken(role, userId, email);

        refreshTokenService.saveRefreshToken(email, refreshToken);
        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto reissueToken(String refreshToken) {
        String email = tokenProvider.getEmail(refreshToken);

        if (!refreshTokenService.existedRefreshToken(email))
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND);

        Long userId = tokenProvider.getUserId(refreshToken);
        String role = tokenProvider.getRole(refreshToken);

        String newAccessToken = TOKEN_PREFIX + tokenProvider.createAccessToken(role, userId, email);
        String newRefreshToken = tokenProvider.createRefreshToken(role, userId, email);

        refreshTokenService.saveRefreshToken(email, newRefreshToken);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    public void removeToken(String accessToken, String refreshToken, HttpServletResponse response) {
        deleteAccessToken(accessToken);
        deleteRefreshToken(refreshToken, response);
    }

    private void deleteAccessToken(String accessToken) {
        LocalDateTime accessTokenExpireAt = tokenProvider.getExpiryDate(accessToken);
        blackListTokenService.saveBlackList(accessToken, accessTokenExpireAt);
    }

    private void deleteRefreshToken(String refreshToken, HttpServletResponse response) {
        String username = tokenProvider.getEmail(refreshToken);
        cookieUtil.deleteCookie(REFRESH_COOKIE_VALUE, response);
        refreshTokenService.deleteRefreshToken(username);
    }
}