package Gotcha.domain.lulu.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum LuLuExceptionCode implements ExceptionCode {
    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "LULU-500-001", "AI 서버 내부 오류입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
