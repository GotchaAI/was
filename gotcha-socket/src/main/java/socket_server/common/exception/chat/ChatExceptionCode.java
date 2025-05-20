package socket_server.common.exception.chat;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ChatExceptionCode implements ExceptionCode {
    GUEST_CANNOT_CHAT(HttpStatus.FORBIDDEN, "CHAT_403_001", "게스트는 채팅을 보낼 수 없습니다.");

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
}
