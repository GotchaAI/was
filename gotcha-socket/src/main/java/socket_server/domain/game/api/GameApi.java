package socket_server.domain.game.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import socket_server.domain.game.dto.GameReadyStatus;

@Tag(name = "[게임 소켓 API]", description = "게임 소켓 관련 API")
public interface GameApi {
    public void enterGameRoom(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId);

    public void sendReadyStatus(@DestinationVariable String roomId, @Payload GameReadyStatus readyStatus);

}
