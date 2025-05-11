package gotcha_auth.jwt;

import gotcha_common.redis.RedisProperties;
import gotcha_common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {

    private final RedisUtil redisUtil;

    public void saveBlackList(String accessToken, LocalDateTime expireAt) {

        String key = RedisProperties.BLACKLIST_KEY_PREFIX + accessToken;
        LocalDateTime now = LocalDateTime.now();
        long timeToLive = Duration.between(now, expireAt).toSeconds();

        redisUtil.setData(key, accessToken);
        redisUtil.setDataExpire(key, timeToLive);
    }

    public boolean existsBlackListCheck(String accessToken) {
        return redisUtil.existed(RedisProperties.BLACKLIST_KEY_PREFIX + accessToken);
    }

}
