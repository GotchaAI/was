package socket_server.domain.chat.api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import socket_server.domain.chat.dto.ChatMessageReq;

@Tag(name = "[채팅 소켓 API]", description = "채팅 소켓 관련 API")
public interface ChattingApi {

    public void sendMessageToAll(@Payload ChatMessageReq message, @Header("simpSessionId") String sessionId);

    public void sendPrivateMessage(@Payload ChatMessageReq message);

    public void sendRoomMessage(@DestinationVariable String roomId, @Payload ChatMessageReq message);
}
