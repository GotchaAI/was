package socket_server.domain.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.chat.ChatMessage;
import gotcha_domain.chat.ChatType;
import gotcha_domain.user.MessageType;
import gotcha_domain.user.Role;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.chat.ChatExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.chat.dto.ChatMessageReq;
import socket_server.domain.chat.service.ChatLogService;

import java.time.LocalDateTime;

import static socket_server.common.constants.WebSocketConstants.CHAT_ALL_CHANNEL;
import static socket_server.common.constants.WebSocketConstants.CHAT_PRIVATE_CHANNEL;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller
@MessageMapping("/chat")
public class ChattingController {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;
    private final ChatLogService chatLogService;
    private final UserService userService;
    private final ErrorType CHAT_ERROR = ErrorType.CHAT;

    public ChattingController(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                              JsonSerializer jsonSerializer,
                              ChatLogService chatLogService,
                              UserService userService) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
        this.chatLogService = chatLogService;
        this.userService = userService;
    }

    // 1. 전체 채팅방 메시지 전송
    @MessageMapping("/all")
    public void sendMessageToAll(@Payload ChatMessageReq messageReq, @AuthenticationPrincipal SecurityUserDetails userDetails) throws JsonProcessingException {
        validateChatPermission(userDetails);

        ChatMessage message = new ChatMessage(
                userDetails.getNickname(),
                messageReq.content(),
                ChatType.ALL,
                LocalDateTime.now()
        );

        RedisMessage redisMessage = new RedisMessage(
                null,
                CHAT_ALL_CHANNEL,
                jsonSerializer.serialize(message, ErrorType.CHAT)
        );

        redisTemplate.convertAndSend(CHAT_ALL_CHANNEL, jsonSerializer.serialize(redisMessage, ErrorType.CHAT));

        chatLogService.saveChatMessage(ChatType.ALL, null, message, userDetails.getUuid());
    }

    // 2. 귓속말 전송
    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload ChatMessageReq messageReq, @AuthenticationPrincipal SecurityUserDetails userDetails) throws JsonProcessingException {
        validateChatPermission(userDetails);
        User sender = userService.findUserByUuidWithFriends(userDetails.getUuid()); // 발신자 정보 조회 (친구 포함)
        User receiver = userService.findUserByNicknameWithFriends(messageReq.receiverNickname()); // 수신자 정보 조회 (친구 포함)

        // 수신자 채팅 옵션 확인 로직 추가
        if (!receiver.canReceiveMessage(MessageType.PRIVATE, sender)) {
            throw new SocketCustomException(CHAT_ERROR, ChatExceptionCode.RECIPIENT_DENIED_PRIVATE_CHAT);
        }

        String receiverUuid = receiver.getUuid();

        ChatMessage message = new ChatMessage(
                userDetails.getNickname(),
                messageReq.content(),
                ChatType.PRIVATE,
                LocalDateTime.now()
        );

        RedisMessage redisMessage = new RedisMessage(
                receiverUuid,
                CHAT_PRIVATE_CHANNEL + receiverUuid,
                jsonSerializer.serialize(message, ErrorType.CHAT)
        );

        redisTemplate.convertAndSend(CHAT_PRIVATE_CHANNEL + receiverUuid, jsonSerializer.serialize(redisMessage, ErrorType.CHAT));

        chatLogService.saveChatMessage(ChatType.PRIVATE, receiverUuid, message, userDetails.getUuid() );
    }

    private void validateChatPermission(SecurityUserDetails userDetails) {
        if (userDetails.getRole().equals(Role.GUEST)) {
            throw new SocketCustomException(CHAT_ERROR, ChatExceptionCode.GUEST_CANNOT_CHAT);
        }
    }
}
