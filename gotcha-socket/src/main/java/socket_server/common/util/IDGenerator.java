package socket_server.common.util;

import gotcha_common.exception.CustomException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.exception.room.RoomExceptionCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Room, Game ID 생성용 전역 유틸 클래스
 */
@Service
public class IDGenerator {
    private static final String ROOM_POOL_KEY = "room:ids:available";
    private static final String GAME_POOL_KEY = "game:ids:available";

    private final RedisTemplate<String, String> redisTemplate;

    public IDGenerator(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //애플리케이션 시작 시 한 번만 실행되며,
    //풀 키가 비어 있으면 0000~9999 문자열을 모두 SADD로 채웁니다.
    @PostConstruct
    private void initRoomPoolIfNeeded() {
        Boolean exists = redisTemplate.hasKey(ROOM_POOL_KEY);
        if (Boolean.FALSE.equals(exists) || redisTemplate.opsForSet().size(ROOM_POOL_KEY) == 0) {
            Set<String> allIds = new HashSet<>(10000);
            for (int i = 0; i < 10000; i++) {
                allIds.add(String.format("%04d", i));
            }
            redisTemplate.opsForSet().add(ROOM_POOL_KEY, allIds.toArray(new String[0]));
        }
    }

    @PostConstruct
    private void initGamePoolIfNeeded() {
        Boolean exists = redisTemplate.hasKey(GAME_POOL_KEY);
        if (Boolean.FALSE.equals(exists) || redisTemplate.opsForSet().size(GAME_POOL_KEY) == 0) {
            Set<String> allIds = new HashSet<>(10000);
            for (int i = 0; i < 10000; i++) {
                allIds.add(String.format("%04d", i));
            }
            redisTemplate.opsForSet().add(GAME_POOL_KEY, allIds.toArray(new String[0]));
        }
    }

    public String allocateGameId() {
        String id = redisTemplate.opsForSet().pop(GAME_POOL_KEY);
        if (id == null) {
            throw new CustomException(GameExceptionCode.GAME_ID_EXHAUSTED);
        }
        return id;
    }

    public void releaseGameId(String id) {
        if (id == null || (!id.matches("\\d{4}"))) {
            throw new CustomException(GameExceptionCode.INVALID_GAME_ID);
        }
        redisTemplate.opsForSet().add(GAME_POOL_KEY, id);
    }

    public String allocateRoomId() {
        String id = redisTemplate.opsForSet().pop(ROOM_POOL_KEY);
        if (id == null) {
            throw new CustomException(RoomExceptionCode.ROOM_ID_EXHAUSTED);
        }
        return id;
    }

    public void releaseRoomId(String id) {
        if (id == null || (!id.matches("\\d{4}"))) {
            throw new CustomException(RoomExceptionCode.INVALID_ROOM_ID);
        }
        redisTemplate.opsForSet().add(ROOM_POOL_KEY, id);
    }

}
