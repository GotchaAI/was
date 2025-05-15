package socket_server.domain.room.service;

import gotcha_common.exception.CustomException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.exception.room.RoomExceptionCode;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoomIdService {
    private static final String POOL_KEY = "room:ids:available";
    private final RedisTemplate<String, String> redisTemplate;

    public RoomIdService(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //애플리케이션 시작 시 한 번만 실행되며,
    //풀 키가 비어 있으면 0000~9999 문자열을 모두 SADD로 채웁니다.
    @PostConstruct
    private void initPoolIfNeeded() {
        Boolean exists = redisTemplate.hasKey(POOL_KEY);
        if (Boolean.FALSE.equals(exists) || redisTemplate.opsForSet().size(POOL_KEY) == 0) {
            Set<String> allIds = new HashSet<>(10000);
            for (int i = 0; i < 10000; i++) {
                allIds.add(String.format("%04d", i));
            }
            redisTemplate.opsForSet().add(POOL_KEY, allIds.toArray(new String[0]));
        }
    }

    // 사용 가능한 ID를 하나 꺼내서 반환합니다.
    // 풀에서 꺼낸 ID는 즉시 제거되므로 중복 할당 되지 않습니다.
    public String allocateRoomId() {
        String id = redisTemplate.opsForSet().pop(POOL_KEY);
        if (id == null) {
            throw new CustomException(RoomExceptionCode.ROOM_ID_EXHAUSTED);
        }
        return id;
    }

    //방 삭제 시 호출하여 ID를 풀에 다시 추가합니다.
    public void releaseRoomId(String id) {
        if (id == null || (!id.matches("\\d{4}"))) {
            throw new CustomException(RoomExceptionCode.INVALID_ROOM_ID);
        }
        redisTemplate.opsForSet().add(POOL_KEY, id);
    }
}
