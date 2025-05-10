package Gotcha.domain.auth.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {

    INVALID_USERNAME_AND_PASSWORD(HttpStatus.NOT_FOUND, "AUTH-404-001", "아이디 또는 비밀번호가 유효하지 않습니다."),
    INVALID_GUEST(HttpStatus.BAD_REQUEST, "AUTH-400-001", "게스트가 아닙니다."),
    INVALID_USERID(HttpStatus.NOT_FOUND, "AUTH-404-002", "존재하지 않는 사용자입니다.");

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
