package socket_server.domain.chat.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import gotcha_domain.chat.ChatMessage;
import gotcha_domain.chat.ChatType;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatLogService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;
    private final ErrorType CHAT_ERROR = ErrorType.CHAT;
    private static final String PRIVATE_CHAT_KEYS_INDEX = "chat:private:index"; // 인덱스 Set을 위한 키

    public ChatLogService(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                          JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    private String chatKey(ChatType chatType, String identifier, String senderId) {
        return switch (chatType) {
            case ALL -> "chat:all:log";
            case ROOM -> "chat:room:" + identifier + ":log";
            case PRIVATE -> privateChatKey(senderId, identifier);
        };
    }

    public void saveChatMessage(ChatType chatType, String identifier, ChatMessage message, String senderId) {
        String key = chatKey(chatType, identifier, senderId);
        String serialized = jsonSerializer.serialize(message, CHAT_ERROR);
        double score = Instant.now().toEpochMilli();

        redisTemplate.opsForZSet().add(key, serialized, score);

        // PRIVATE 채팅일 경우, 키 인덱스 Set에 키를 추가
        if (chatType == ChatType.PRIVATE) {
            redisTemplate.opsForSet().add(PRIVATE_CHAT_KEYS_INDEX, key);
        }
    }

    public void removeExpiredMessages(ChatType chatType, String identifier) {
        String key = chatKey(chatType, identifier, null);
        long cutoff = Instant.now().minus(Duration.ofHours(1)).toEpochMilli();
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, cutoff);
    }

    public void removeExpiredMessagesByKey(String fullKey) {
        long cutoff = Instant.now().minus(Duration.ofHours(1)).toEpochMilli();
        redisTemplate.opsForZSet().removeRangeByScore(fullKey, 0, cutoff);
    }

    public List<ChatMessage> getRecentMessages(ChatType chatType, String identifier, String senderId, int count) {
        String key = chatKey(chatType, identifier, senderId);
        Set<String> rawMessages = redisTemplate.opsForZSet().range(key, 0, count - 1);

        if (rawMessages == null) return List.of();

        return rawMessages.stream()
                .map(raw -> jsonSerializer.deserialize(raw, ChatMessage.class, CHAT_ERROR))
                .collect(Collectors.toList());
    }

    //신고한 채팅을 기준으로 앞뒤 10개의 채팅 가져오기
    public List<ChatMessage> getSurroundingMessages(ChatType chatType, String identifier, String senderId, LocalDateTime baseTime, int range) {
        String key = chatKey(chatType, identifier, senderId);
        long baseTimestamp = baseTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // 기준 메시지
        Set<String> baseMessageRaw = redisTemplate.opsForZSet()
                .rangeByScore(key, baseTimestamp, baseTimestamp);

        // 앞쪽 메시지 (기준 이전 메시지들)
        Set<String> beforeRaw = redisTemplate.opsForZSet()
                .reverseRangeByScore(key, 0, baseTimestamp - 1, 0, range);

        // 뒤쪽 메시지 (기준 이후 메시지들)
        Set<String> afterRaw = redisTemplate.opsForZSet()
                .rangeByScore(key, baseTimestamp + 1, Double.MAX_VALUE, 0, range);

        // 병합 후 정렬 (총 20개)
        List<String> combined = new ArrayList<>(beforeRaw);
        combined.addAll(baseMessageRaw);
        combined.addAll(afterRaw);

        return combined.stream()
                .map(raw -> jsonSerializer.deserialize(raw, ChatMessage.class, CHAT_ERROR))
                .sorted(Comparator.comparing(ChatMessage::sentAt))
                .collect(Collectors.toList());
    }

    public Cursor<String> scanPrivateChatKeys() {
        ScanOptions options = ScanOptions.scanOptions().count(1000).build();
        return redisTemplate.opsForSet().scan(PRIVATE_CHAT_KEYS_INDEX, options);
    }

    /**
     * @deprecated Redis에 저장된 데이터의 양이 많아지면 SMEMBERS보다 SSCAN을 이용해 순차적으로 데이터를 받아오는게 더 효율적이라 SSCAN 방식으로 변경함
     */
    @Deprecated
    public Set<String> getPrivateChatKeys() {
        // KEYS 대신 SMEMBERS를 사용하여 안전하고 빠르게 키 목록을 가져옴
        Set<String> keys = redisTemplate.opsForSet().members(PRIVATE_CHAT_KEYS_INDEX);
        return keys != null ? keys : Set.of();
    }

    private String privateChatKey(String userId1, String userId2) {
        List<String> sorted = List.of(userId1, userId2).stream().sorted().toList();
        return "chat:private:" + sorted.get(0) + ":" + sorted.get(1) + ":log";
    }
}