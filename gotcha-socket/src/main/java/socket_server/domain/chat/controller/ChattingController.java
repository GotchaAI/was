package socket_server.domain.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.user.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.chat.ChatExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.chat.dto.ChatMessage;
import socket_server.domain.chat.dto.ChatMessageReq;
import socket_server.domain.chat.dto.ChatType;

import java.time.LocalDateTime;

import static socket_server.common.constants.WebSocketConstants.CHAT_ALL_CHANNEL;
import static socket_server.common.constants.WebSocketConstants.CHAT_PRIVATE_CHANNEL;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller
@MessageMapping("/chat")
public class ChattingController {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;

    public ChattingController(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                              JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
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
                jsonSerializer.serialize(message)
        );

        redisTemplate.convertAndSend(CHAT_ALL_CHANNEL, jsonSerializer.serialize(redisMessage));
    }

    // 2. 귓속말 전송
    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload ChatMessageReq messageReq, @AuthenticationPrincipal SecurityUserDetails userDetails) throws JsonProcessingException {
        validateChatPermission(userDetails);

        ChatMessage message = new ChatMessage(
                userDetails.getNickname(),
                messageReq.content(),
                ChatType.PRIVATE,
                LocalDateTime.now()
        );

        RedisMessage redisMessage = new RedisMessage(
                messageReq.receiverUuid(),
                CHAT_PRIVATE_CHANNEL + messageReq.receiverUuid(),
                jsonSerializer.serialize(message)
        );

        redisTemplate.convertAndSend(CHAT_PRIVATE_CHANNEL + messageReq.receiverUuid(), jsonSerializer.serialize(redisMessage));
    }

//    // 3. 대기방 내 채팅
//    @MessageMapping("/room/{roomId}")
//    public void sendRoomMessage(@DestinationVariable String roomId,
//                                @Payload ChatMessageReq messageReq,
//                                @AuthenticationPrincipal SecurityUserDetails userDetails) throws JsonProcessingException {
//        ChatMessage message = new ChatMessage(
//                userDetails.getNickname(),
//                messageReq.content(),
//                ChatType.ROOM,
//                LocalDateTime.now()
//        );
//
//        RedisMessage redisMessage = new RedisMessage(
//                null,
//                CHAT_ROOM_CHANNEL + roomId,
//                jsonSerializer.serialize(message)
//        );
//
//        redisTemplate.convertAndSend(CHAT_ROOM_CHANNEL + roomId, jsonSerializer.serialize(redisMessage));
//    }

    private void validateChatPermission(SecurityUserDetails userDetails) {
        if (userDetails.getRole().equals(Role.GUEST)) {
            throw new CustomException(ChatExceptionCode.GUEST_CANNOT_CHAT);
        }
    }
}
