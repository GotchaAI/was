package socket_server.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class SocketFieldValidationException extends RuntimeException {
    private final ErrorType source;
    private final Map<String, String> fieldErrors;

    public SocketFieldValidationException(ErrorType source, Map<String, String> errors) {
        super(source + " : 입력값 검증에 실패했습니다.");
        this.source = source;
        this.fieldErrors = errors;
    }

    public SocketFieldValidationException(ErrorType source, String field, String message) {
        super(source + " : 입력값 검증에 실패했습니다.");
        this.source=source;
        this.fieldErrors = Map.of(field, message);
    }

}
