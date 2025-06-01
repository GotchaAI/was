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

    public Set<String> getPrivateChatKeys() {
        Set<String> keys = redisTemplate.keys("chat:private:*:*:log");
        return keys != null ? keys : Set.of();
    }

    private String privateChatKey(String userId1, String userId2) {
        List<String> sorted = List.of(userId1, userId2).stream().sorted().toList();
        return "chat:private:" + sorted.get(0) + ":" + sorted.get(1) + ":log";
    }
}
