package Gotcha.common.jwt.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {
    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final long autoRefreshExpiration;
    private final String issuer;

    public TokenProvider(@Value("${jwt.secret-key}") String secret_key,
                         @Value("${jwt.access-expiration}") long accessExpiration,
                         @Value("${jwt.refresh-expiration}") long refreshExpiration,
                         @Value("${jwt.auto-login-refresh-expiration}") long autoRefreshExpiration,
                         @Value("${jwt.issuer}") String issuer) {
        this.secretKey = new SecretKeySpec(secret_key.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.autoRefreshExpiration = autoRefreshExpiration;
        this.issuer = issuer;
    }

    public String createAccessToken(String role, Long userId, String username) {
        return createToken(makeAccessTokenClaims(role, userId), username, accessExpiration);
    }

    public String createRefreshToken(String role, Long userId, String username, boolean autoSignIn) {
        return createToken(makeRefreshTokenClaims(role, userId, autoSignIn), username, autoSignIn?autoRefreshExpiration:refreshExpiration);
    }

    private Map<String, Object> makeAccessTokenClaims(String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        return claims;
    }

    private Map<String, Object> makeRefreshTokenClaims(String role, Long userId, boolean autoSignIn) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        claims.put("auto", autoSignIn);
        return claims;
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiry) {
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(subject)
                .claims(claims)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(secretKey)
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAutoSignIn(String token) {
        Claims claims = getClaims(token);
        Boolean auto = claims.get("auto", Boolean.class);
        return Boolean.TRUE.equals(auto);
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public LocalDateTime getExpiryDate(String token) {
        return getClaims(token).getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}