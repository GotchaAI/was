package Gotcha.common.jwt;

import Gotcha.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static Gotcha.common.redis.RedisProperties.REFRESH_TOKEN_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisUtil redisUtil;
    private final TokenProvider tokenProvider;

    @Value("${token.refresh.in-redis}")
    private long REFRESH_EXPIRATION;

    public void saveRefreshToken(String email, String refreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + email;
        redisUtil.setData(key, refreshToken);
        redisUtil.setDataExpire(key, REFRESH_EXPIRATION);
    }

    public void deleteRefreshToken(String refreshToken) {
        String email = tokenProvider.getEmail(refreshToken);
        String key = REFRESH_TOKEN_KEY_PREFIX + email;
        redisUtil.deleteData(key);
    }

    public boolean existedRefreshToken(String email, String requestRefreshToken) {
        String key = REFRESH_TOKEN_KEY_PREFIX + email;

        String storedRefreshToken = (String) redisUtil.getData(key);

        if (storedRefreshToken == null) {
            return false;
        }

        return storedRefreshToken.equals(requestRefreshToken);
    }
}
