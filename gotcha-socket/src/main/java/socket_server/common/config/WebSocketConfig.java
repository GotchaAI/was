package socket_server.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import socket_server.common.auth.StompPrincipalHandshakeHandler;
import socket_server.common.auth.StompUserInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StompUserInterceptor());
    }

    //레디스 메시지 브로커 사용
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub", "/user"); // 구독 (Subscribe)
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 (Publish)
        registry.setUserDestinationPrefix("/user");
    }

    //초기 핸드쉐이크 과정에서 사용할 endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect") //https로 변경시 wws로 변경
                .setHandshakeHandler(new StompPrincipalHandshakeHandler())
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SockJS 지원

        registry.addEndpoint("/ws-connect") //todo : 실제 배포시엔 sockJs만 이용하므로, 해당 코드 삭제 필요
                .setHandshakeHandler(new StompPrincipalHandshakeHandler())
                .setAllowedOriginPatterns("*");
    }

}

