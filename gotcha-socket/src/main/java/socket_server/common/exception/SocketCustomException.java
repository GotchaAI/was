package socket_server.common.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocketCustomException extends RuntimeException {
    private final ErrorType errorType;
    private final ExceptionCode exceptionCode;

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
