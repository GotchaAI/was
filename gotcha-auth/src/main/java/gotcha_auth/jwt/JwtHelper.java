package gotcha_auth.jwt;

import gotcha_auth.dto.TokenDto;
import gotcha_auth.exception.JwtExceptionCode;
import gotcha_common.exception.CustomException;
import gotcha_common.util.CookieUtil;
import gotcha_domain.user.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class JwtHelper {

    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final BlackListTokenService blackListTokenService;

    public TokenDto createToken(User user, boolean autoSignIn) {
        String uuid = user.getUuid();
        String username = user.getNickname();
        return getTokenDto(user, uuid, username, autoSignIn);
    }

    public TokenDto createGuestToken(User guest) {
        String uuid = guest.getUuid();
        String username = guest.getNickname();
        return getTokenDto(guest, uuid, username, false);
    }

    private TokenDto getTokenDto(User user, String uuid, String username, boolean autoSignIn) {
        String role = String.valueOf(user.getRole());

        String accessToken = JwtProperties.TOKEN_PREFIX + tokenProvider.createAccessToken(role, uuid, username);
        String refreshToken = tokenProvider.createRefreshToken(role, uuid, username, autoSignIn);
        LocalDateTime accessTokenExpiredAt = tokenProvider.getExpiryDate(
                accessToken.replace(JwtProperties.TOKEN_PREFIX, "").trim()
        );

        refreshTokenService.saveRefreshToken(uuid, refreshToken);
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt, autoSignIn);
    }

    public TokenDto reissueToken(String refreshToken) {
        refreshTokenService.isExpiredRefreshToken(refreshToken);

        String username = tokenProvider.getUsername(refreshToken);
        String uuid = tokenProvider.getUuid(refreshToken);

        if (!refreshTokenService.existedRefreshToken(uuid, refreshToken)) {
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String role = tokenProvider.getRole(refreshToken);

        boolean autoSignIn = tokenProvider.isAutoSignIn(refreshToken);

        String newAccessToken = JwtProperties.TOKEN_PREFIX + tokenProvider.createAccessToken(role, uuid, username);
        String newRefreshToken = tokenProvider.createRefreshToken(role, uuid, username, autoSignIn);
        LocalDateTime newAccessTokenExpiredAt = tokenProvider.getExpiryDate(
                newAccessToken.replace(JwtProperties.TOKEN_PREFIX, "").trim()
        );

        refreshTokenService.deleteRefreshToken(refreshToken);
        refreshTokenService.saveRefreshToken(username, newRefreshToken);

        return new TokenDto(newAccessToken, newRefreshToken, newAccessTokenExpiredAt, autoSignIn);
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
        cookieUtil.deleteCookie(JwtProperties.REFRESH_COOKIE_VALUE, response);
        refreshTokenService.deleteRefreshToken(refreshToken);
    }
}