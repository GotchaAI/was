package Gotcha.common.jwt;

import Gotcha.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static Gotcha.common.redis.RedisProperties.BLACKLIST_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class BlackListTokenService {

    private final RedisUtil redisUtil;

    public void saveBlackList(String accessToken, LocalDateTime expireAt) {

        String key = BLACKLIST_KEY_PREFIX + accessToken;
        LocalDateTime now = LocalDateTime.now();
        long timeToLive = Duration.between(now, expireAt).toSeconds();

        redisUtil.setData(key, accessToken);
        redisUtil.setDataExpire(key, timeToLive);
    }

    public boolean existsBlackListCheck(String accessToken) {
        return redisUtil.existed(BLACKLIST_KEY_PREFIX + accessToken);
    }

}
