package socket_server.domain.chat.handler;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.listener.PubSubHandler;
import socket_server.domain.chat.dto.ChatMessageReq;

import static socket_server.common.constants.WebSocketConstants.*;

@Service
public class ChattingPubSubHandler extends PubSubHandler {

    public ChattingPubSubHandler(SimpMessagingTemplate messagingTemplate) {
        super(messagingTemplate);
    }

    @Override
    protected void initHandlers() {
        handlers.put(CHAT_ALL_CHANNEL, (channel, message) -> handleAllChat(message));
        handlers.put(CHAT_PRIVATE_CHANNEL, this:: handlePrivateChat);
        handlers.put(CHAT_ROOM_CHANNEL, this:: handleRoomChat);
    }

    // 채팅 처리 메소드
    private void handleAllChat(String message) {
        ChatMessageReq chatMessageReq = convertMessageToDto(message, ChatMessageReq.class);
        messagingTemplate.convertAndSend(CHAT_ALL_CHANNEL, chatMessageReq);  // 전체 채팅방 메시지 처리
    }

    private void handlePrivateChat(String channel, String message) {
        String nickName = channel.replace(CHAT_PRIVATE_CHANNEL, "");
        messagingTemplate.convertAndSend(CHAT_PRIVATE_CHANNEL + nickName, message);  // 귓속말 처리
    }

    private void handleRoomChat(String channel, String message) {
        String roomId = channel.replace(CHAT_ROOM_CHANNEL, "");
        ChatMessageReq chatMessageReq = convertMessageToDto(message, ChatMessageReq.class);
        messagingTemplate.convertAndSend(CHAT_ROOM_CHANNEL + roomId, chatMessageReq);  // 대기방 채팅 처리
    }
}
