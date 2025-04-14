package Gotcha.domain.auth.dto;

import java.time.LocalDateTime;

public record TokenDto(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiredAt,
        boolean autoSignIn
) {
    public static TokenDto of(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt) {
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt, false);
    }
    public static TokenDto of(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt, boolean autoSignIn) {
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt, autoSignIn);
    }
}