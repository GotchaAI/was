package socket_server.domain.chat.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.chat.dto.ChatMessage;
import socket_server.domain.chat.dto.ChatType;

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


    public Set<String> getPrivateChatKeys() {
        Set<String> keys = redisTemplate.keys("chat:private:*:*:log");
        return keys != null ? keys : Set.of();
    }

    private String privateChatKey(String userId1, String userId2) {
        List<String> sorted = List.of(userId1, userId2).stream().sorted().toList();
        return "chat:private:" + sorted.get(0) + ":" + sorted.get(1) + ":log";
    }
}
