package Gotcha.domain.auth.dto;

import java.time.LocalDateTime;

public record TokenDto(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiredAt
) {
    public static TokenDto of(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt) {
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt);
    }
}