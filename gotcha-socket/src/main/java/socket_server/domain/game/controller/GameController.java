package socket_server.domain.game.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import socket_server.domain.game.dto.GameReadyStatus;

import static socket_server.common.constants.WebSocketConstants.GAME_READY_CHANNEL;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller("/game")
@RequiredArgsConstructor
public class GameController {
    private final RedisTemplate<String, Object> pubSubHandler;

    // 1. 대기방 입장시 채널 관리
    @MessageMapping("/enter/room/{roomId}")
    public void enterGameRoom(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
    }

    // 3. 대기방 레디 상태 업데이트
    @MessageMapping("/ready/{roomId}")
    public void sendReadyStatus(@DestinationVariable String roomId, @Payload GameReadyStatus readyStatus) {
        pubSubHandler.convertAndSend(GAME_READY_CHANNEL   + roomId, readyStatus);
    }

    //todo : 5. 게임 시작 전 게임 정보(제시어) 전달
    @MessageMapping("/info/{roomId}")
    public void sendGameInfoBeforeStart(@DestinationVariable String roomId, String roundCount) { //라운드 수 같이 줘야 함.
    }

    // todo : 6. 게임 시작 ( 레디 상태가 최소 인원 넘는지 확인,
    @MessageMapping("/start/{roomId}")
    public void startGame(@DestinationVariable String roomId, @Payload String nickName, @Header("simpSessionId") String sessionId) {

    }

    // todo : 7. 게임 종료
    @MessageMapping("/end/{roomId}")
    public void endGame(@DestinationVariable String roomId, @Payload String nickName,  @Header("simpSessionId") String sessionId) {
    }

    //todo : 8. 대기방 퇴장
    @MessageMapping("/exit/{roomId}")
    public void exitGameRoom(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
    }


}
