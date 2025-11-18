package socket_server.common.auth;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import socket_server.common.exception.socket.SocketUserStatusExceptionCode;

@Component
@RequiredArgsConstructor
public class UserStatusInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        try {
            if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                Authentication auth = (Authentication) accessor.getUser();
                SecurityUserDetails details = (SecurityUserDetails) auth.getPrincipal();
                User user = details.getUser();

                SocketUserStatusExceptionCode code =
                        SocketUserStatusExceptionCode.fromStatus(user.getUserStatus());

                if (code != null) {
                    throw new MessagingException(toErrorPayload(code));
                }

            }

            return message;
        } catch (Throwable e) {
            if (e instanceof MessagingException) {
                throw (MessagingException) e;
            }
            System.err.println("=== [DEBUG][UserStatusInterceptor] 예외 발생 ===");
            e.printStackTrace();

            throw new MessagingException(toErrorPayload(GlobalExceptionCode.INTERNAL_SERVER_ERROR));
        }
    }

    private String toErrorPayload(ExceptionCode code) {
        return String.format("{\"errorCode\":\"%s\", \"status\":%d, \"message\":\"%s\"}", code.getCode(),
                code.getStatus().value(), code.getMessage());
    }
}
