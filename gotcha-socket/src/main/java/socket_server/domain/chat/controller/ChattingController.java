package socket_server.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import socket_server.domain.chat.api.ChattingApi;
import socket_server.domain.chat.dto.ChatMessageReq;

import static socket_server.common.constants.WebSocketConstants.*;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller("/chat")
@RequiredArgsConstructor
public class ChattingController implements ChattingApi {
    private final RedisTemplate<String, String> redisTemplate;

    // 1. 전체 채팅방 메시지 전송
    @MessageMapping("/all")
    public void sendMessageToAll(@Payload ChatMessageReq message, @Header("simpSessionId") String sessionId) {
        // 현재 세션이 구독한 채널 조회
        redisTemplate.convertAndSend(CHAT_ALL_CHANNEL, message.toJson());
    }

    // 2. 귓속말 전송
    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload ChatMessageReq message) {
        redisTemplate.convertAndSend(CHAT_PRIVATE_CHANNEL + message.nickName(), message.toJson());
    }

    // 3. 대기방 내 채팅
    @MessageMapping("/room/{roomId}")
    public void sendRoomMessage(@DestinationVariable String roomId, @Payload ChatMessageReq message) {
        redisTemplate.convertAndSend(CHAT_ROOM_CHANNEL + roomId, message);
    }

}
