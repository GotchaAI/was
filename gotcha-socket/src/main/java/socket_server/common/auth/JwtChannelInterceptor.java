package socket_server.common.auth;

import gotcha_auth.exception.JwtExceptionCode;
import gotcha_auth.jwt.JwtAuthService;
import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    public static final String AUTHORIZATION = "Authorization";
    private final JwtAuthService jwtAuthService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {

                String tokenHeader = accessor.getFirstNativeHeader(AUTHORIZATION);
                Authentication authentication = jwtAuthService.authenticate(tokenHeader);
                accessor.setUser(authentication);

            } catch (ExpiredJwtException e) {
                throw new MessagingException(toErrorPayload(JwtExceptionCode.ACCESS_TOKEN_EXPIRED));
            } catch (SignatureException e) {
                throw new MessagingException(toErrorPayload(JwtExceptionCode.INVALID_TOKEN_SIGNATURE));
            } catch (JwtException | IllegalArgumentException e) {
                throw new MessagingException(toErrorPayload(JwtExceptionCode.UNKNOWN_TOKEN_ERROR));
            } catch (UsernameNotFoundException e) {
                throw new MessagingException(toErrorPayload(GlobalExceptionCode.USER_NOT_FOUND));
            } catch (AuthenticationServiceException e) {
                throw new MessagingException(toErrorPayload(JwtExceptionCode.ACCESS_TOKEN_NOT_FOUND));
            }
        }
        return message;
    }

    private String toErrorPayload(ExceptionCode code) {
        return String.format(
                "{\"errorCode\":\"%s\", \"status\":%d, \"message\":\"%s\"}",
                code.getCode(),
                code.getStatus().value(),
                code.getMessage()
        );
    }



}
