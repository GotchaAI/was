package socket_server.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //레디스 메시지 브로커 사용
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 구독 (Subscribe)
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 (Publish)
    }

    //초기 핸드쉐이크 과정에서 사용할 endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect") //https로 변경시 wws로 변경
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SockJS 지원

        registry.addEndpoint("/ws-connect") // todo : 실제 배포시엔 삭제해야함
                .setAllowedOriginPatterns("*");
    }

}

