package socket_server.domain.chat.handler;

import gotcha_common.util.RedisUtil;
import gotcha_domain.chat.ChatMessage;
import gotcha_domain.user.ChatOption;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;

import static socket_server.common.constants.WebSocketConstants.CHAT_ALL_CHANNEL;
import static socket_server.common.constants.WebSocketConstants.CHAT_PRIVATE_CHANNEL;
import static socket_server.common.constants.WebSocketConstants.CHAT_ROOM_CHANNEL;

@Slf4j
@Service
@Qualifier("chattingPubSubHandler")
public class ChattingPubSubHandler extends PubSubHandler {
    private final SimpUserRegistry userRegistry;
    private final UserService userService;
    private final RedisUtil redisUtil;

    public ChattingPubSubHandler(SimpMessagingTemplate messagingTemplate,
                                 JsonSerializer jsonSerializer,
                                 SimpUserRegistry userRegistry,
                                 UserService userService,
                                 RedisUtil redisUtil) {
        super(messagingTemplate, jsonSerializer);
        this.userRegistry = userRegistry;
        this.userService = userService;
        this.redisUtil = redisUtil;
    }

    @Override
    protected void initHandlers() {
        handlers.put(CHAT_ALL_CHANNEL, (channel, message) -> {
            try {
                handleAllChat(message);
            } catch (Exception e) {
                log.error("[ChattingPubSubHandler] handleAllChat 처리 중 에러 발생", e);
            }
        });
        handlers.put(CHAT_PRIVATE_CHANNEL, (channel, message) -> {
            try {
                handlePrivateChat(channel, message);
            } catch (Exception e) {
                log.error("[ChattingPubSubHandler] handlePrivateChat 처리 중 에러 발생 - 채널:{}", channel, e);
            }
        });
        handlers.put(CHAT_ROOM_CHANNEL, this::handleRoomChat);
    }

    private void handleAllChat(Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        ChatMessage chatMessage = jsonSerializer.deserialize(redisMessage.payload(), ChatMessage.class, ErrorType.CHAT);
        User sender = userService.findUserByNickname(chatMessage.nickname());

        String senderUuid = sender.getUuid();

        for (SimpUser simpUser : userRegistry.getUsers()) {
            String recipientUuid = simpUser.getName();
            if (recipientUuid.equals(senderUuid)) continue;

            try {
                String settingsCacheKey = "user:" + recipientUuid + ":settings";
                String chatOptionStr = (String) redisUtil.hGet(settingsCacheKey, "chatOption");
                ChatOption chatOption = (chatOptionStr != null) ? ChatOption.valueOf(chatOptionStr) : ChatOption.ALLOW_ALL; // Default to ALLOW_ALL if not cached

                // 1. DENY_ALL 인지 확인
                if (chatOption == ChatOption.DENY_ALL) {
                    continue;
                }

                // 2. FRIENDS_ONLY 인지 확인
                if (chatOption == ChatOption.FRIENDS_ONLY) {
                    String friendCacheKey = "user:" + recipientUuid + ":friends";
                    if (!redisUtil.isSetMember(friendCacheKey, senderUuid)) {
                        continue;
                    }
                }

                // All 은 확인하지 않고 메시지 전송
                messagingTemplate.convertAndSendToUser(
                        recipientUuid,
                        "/queue/chat",
                        chatMessage
                );

            } catch (Exception e) {
                log.error("handleAllChat 처리 중 특정 사용자에게 에러 발생: {}", recipientUuid, e);
            }
        }
    }

    private void handlePrivateChat(String channel, Object object) {
        RedisMessage redisMessage = (RedisMessage) object;
        ChatMessage chatMessage = jsonSerializer.deserialize(redisMessage.payload(), ChatMessage.class, ErrorType.CHAT);

        // Controller에서 이미 권한 체크를 했으므로 바로 전송
        // 프로젝트의 다른 핸들러들과 동일한 패턴으로, 수신한 채널에 그대로 메시지를 보낸다.
        messagingTemplate.convertAndSend(channel, chatMessage);
    }

    private void handleRoomChat(String channel, Object object) {
        // 룸 채팅 로직 (필요시 구현)
    }
}