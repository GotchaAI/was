package socket_server.common.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.security.Principal;
import java.util.UUID;

@Slf4j
// 2) STOMP CONNECT 프레임을 가로채는 Interceptor
public class StompUserInterceptor implements ChannelInterceptor  {

    @Override
    public Message<?> preSend (Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            String rawUserId = accessor.getFirstNativeHeader("user-id");

            String userId = (rawUserId == null || rawUserId.isBlank())
                    ? "guest-" + UUID.randomUUID()
                    : rawUserId;

            Principal principal = () -> userId;
            accessor.setUser(principal);
        }
        return message;
    }


}

