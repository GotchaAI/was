package socket_server.common.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller
@RequiredArgsConstructor
public class StompConnectionController {

    // WebSocket 첫 연결 시 초기 채널 관리
    @MessageMapping("/connect")
    public void onConnect() {
        //추후 세션ID ↔ 유저ID 매핑에 사용. 지금은 사용 x
    }

}
