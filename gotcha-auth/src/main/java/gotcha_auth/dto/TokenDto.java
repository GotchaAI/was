package gotcha_auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenDto(
        String accessToken,
        String refreshToken,
        LocalDateTime accessTokenExpiredAt,
        boolean autoSignIn,
        Object warningDetails
) {
    public static TokenDto of(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt) {
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt, false, null);
    }
    public static TokenDto of(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt, boolean autoSignIn) {
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt, autoSignIn, null);
    }
    public static TokenDto of(String accessToken, String refreshToken, LocalDateTime accessTokenExpiredAt, boolean autoSignIn, Object warningDetails) {
        return new TokenDto(accessToken, refreshToken, accessTokenExpiredAt, autoSignIn, warningDetails);
    }
}