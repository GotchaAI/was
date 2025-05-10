package socket_server.common.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import socket_server.common.manager.ChannelManager;

//WebSocket으로 들어온 메시지를 Redis에 발행
@Controller
@RequiredArgsConstructor
public class StompConnectionController {
    private final ChannelManager channelManager;

    // WebSocket 첫 연결 시 초기 채널 관리
    @MessageMapping("/connect")
    public void onConnect(@Payload String nickName, @Header("simpSessionId") String sessionId) {
        channelManager.subscribeToInitialChannels(nickName, sessionId);
    }
    //자동배포 성공 테스트 주석

}
