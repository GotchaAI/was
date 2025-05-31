package socket_server.common.exception;

import gotcha_common.exception.ExceptionRes;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;

import static gotcha_common.exception.exceptionCode.GlobalExceptionCode.USER_NOT_FOUND;
import static socket_server.common.constants.WebSocketConstants.*;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class SocketGlobalExceptionHandler {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(SocketFieldValidationException.class)
    public void handleSocketFieldValidationException(
            SocketFieldValidationException e,
            SimpMessageHeaderAccessor accessor
    ) {
        log.warn("[WebSocket FieldValidationException] {} - {}", e.getSource(), e.getFieldErrors());
        sendErrorToUser(e.getSource(), accessor, ExceptionRes.from(GlobalExceptionCode.FIELD_VALIDATION_ERROR, e.getFieldErrors()));
    }

    @MessageExceptionHandler(SocketCustomException.class)
    public void handleSocketCustomException(
            SocketCustomException e,
            SimpMessageHeaderAccessor accessor
    ) {
        log.warn("[WebSocket CustomException] {} - {}", e.getErrorType(), e.getExceptionCode());
        sendErrorToUser(e.getErrorType(), accessor, ExceptionRes.from(e.getExceptionCode()));
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ExceptionRes handleUnexpectedException(Exception e) {
        log.error("[WebSocket Unexpected Exception] {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ExceptionRes.from(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
    }

    private void sendErrorToUser(ErrorType errorType, SimpMessageHeaderAccessor accessor, ExceptionRes response) {
        String uuid = findNowUuid(accessor, errorType);
        String destination = switch (errorType) {
            case ROOM -> ERROR_CHANNEL_PREFIX + uuid + ERROR_ROOM_CHANNEL;
            case CHAT -> ERROR_CHANNEL_PREFIX + uuid + ERROR_CHAT_CHANNEL;
            case GAME -> ERROR_CHANNEL_PREFIX + uuid + ERROR_GAME_CHANNEL;
            case LOBBY -> ERROR_CHANNEL_PREFIX + uuid + ERROR_LOBBY_CHANNEL;
            default -> ERROR_CHANNEL_PREFIX + uuid + ERROR_DEFAULT_CHANEL ;
        };
        messagingTemplate.convertAndSend(destination, response);
    }

    private static String findNowUuid(SimpMessageHeaderAccessor accessor, ErrorType errorType) {
        Authentication auth = (Authentication) accessor.getUser();
        if (auth == null) {
            throw new SocketCustomException(errorType, USER_NOT_FOUND);
        }
        return ((SecurityUserDetails) auth.getPrincipal()).getUuid();
    }
}


