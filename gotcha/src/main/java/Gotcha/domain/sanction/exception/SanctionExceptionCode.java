package Gotcha.domain.sanction.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum SanctionExceptionCode implements ExceptionCode {
    NOT_SUSPENDED_USER(HttpStatus.BAD_REQUEST, "SANCTION-400-001", "해당 유저는 정지 상태가 아닙니다.");

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
