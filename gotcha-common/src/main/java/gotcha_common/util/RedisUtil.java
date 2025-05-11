package gotcha_common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean existed(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setDataExpire(String key, Long duration) {
        redisTemplate.expire(key, Duration.ofSeconds(duration));
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }
}
