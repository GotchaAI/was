package socket_server.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().permitAll() // 목적지가 null이면 허용 (초기 메세지)
                .simpDestMatchers("/pub/**").authenticated() //인증된 사용자만 메시시 전송 가능
                .simpSubscribeDestMatchers("/sub/**").authenticated() //인증된 사용자만 구독 가능
                .anyMessage().denyAll(); //그 외는 허용x
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}