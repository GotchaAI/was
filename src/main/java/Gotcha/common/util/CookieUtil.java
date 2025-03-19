package Gotcha.common.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${token.refresh.in-cookie}")
    private long COOKIE_REFRESH_EXPIRATION;

    public ResponseCookie createCookie(String key, String value) {

        return ResponseCookie.from(key, value)
                .path("/")
                .httpOnly(true)
                .maxAge(COOKIE_REFRESH_EXPIRATION)
                .secure(true)
                .sameSite("None")
                .build();
    }

    public void deleteCookie(String cookieName, HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

    }
}