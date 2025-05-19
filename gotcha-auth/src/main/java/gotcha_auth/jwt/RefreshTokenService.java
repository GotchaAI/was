package gotcha_auth.jwt;

import gotcha_auth.exception.JwtExceptionCode;
import gotcha_common.exception.CustomException;
import gotcha_common.redis.RedisProperties;
import gotcha_common.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;

    @Value("${token.refresh.in-redis}")
    private long REFRESH_EXPIRATION;

    public void saveRefreshToken(String uuid, String refreshToken) {
        String key = RedisProperties.REFRESH_TOKEN_KEY_PREFIX + uuid;
        redisUtil.setData(key, refreshToken);
        redisUtil.setDataExpire(key, REFRESH_EXPIRATION);
    }

    public void deleteRefreshToken(String refreshToken) {
        String username = tokenProvider.getUsername(refreshToken);
        String key = RedisProperties.REFRESH_TOKEN_KEY_PREFIX + username;
        redisUtil.deleteData(key);
    }

    public boolean existedRefreshToken(String username, String requestRefreshToken) {
        String key = RedisProperties.REFRESH_TOKEN_KEY_PREFIX + username;

        String storedRefreshToken = (String) redisUtil.getData(key);

        if (storedRefreshToken == null) {
            return false;
        }

        return storedRefreshToken.equals(requestRefreshToken);
    }

    public void isExpiredRefreshToken(String refreshToken) {
        try {
            tokenProvider.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(JwtExceptionCode.REFRESH_TOKEN_EXPIRED);
        }
    }
}
