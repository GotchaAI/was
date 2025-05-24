package socket_server.domain.game.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.listener.PubSubHandler;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.GameReadyStatus;

import static socket_server.common.constants.WebSocketConstants.*;

@Service
@Qualifier("gamePubSubHandler")
public class GamePubSubHandler extends PubSubHandler {

    public GamePubSubHandler(SimpMessagingTemplate messagingTemplate, JsonSerializer jsonSerializer) {
        super(messagingTemplate, jsonSerializer);
    }

    @Override
    protected void initHandlers() {
        handlers.put(GAME_READY_CHANNEL, this::handleGameReady);
        handlers.put(GAME_START_CHANNEL , this::handleGameStart);
        handlers.put(GAME_END_CHANNEL, this::handleGameEnd);
        handlers.put(GAME_INFO_CHANNEL, this::handleGameInfo);
    }

    private void handleGameReady(String channel, Object object) {
        GameReadyStatus gameReadyStatus = jsonSerializer.deserialize(object, GameReadyStatus.class);
        messagingTemplate.convertAndSend(channel, gameReadyStatus);
    }

    private void handleGameStart(String channel, Object object) {
        messagingTemplate.convertAndSend(channel, object);
    }

    private void handleGameEnd(String channel, Object object) {
        messagingTemplate.convertAndSend(channel, object);
    }

    private void handleGameInfo(String channel, Object object) {

    }
}
