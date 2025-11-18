package socket_server.common.exception.socket;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_domain.user.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SocketUserStatusExceptionCode implements ExceptionCode {
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "SOCKET-403-001", "계정이 일시 정지되었습니다."),
    USER_BANNED(HttpStatus.FORBIDDEN, "SOCKET-403-002", "계정이 영구 정지되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static SocketUserStatusExceptionCode fromStatus(UserStatus status) {
        return switch (status) {
            case SUSPENDED -> USER_SUSPENDED;
            case BANNED    -> USER_BANNED;
            default        -> null;  // 정상 계정
        };
    }
}
