package Gotcha.domain.auth.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthExceptionCode implements ExceptionCode {

    INVALID_USERNAME_AND_PASSWORD(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호가 유효하지 않습니다."),
    INVALID_USERID(HttpStatus.NOT_FOUND,"존재하지 않는 사용자입니다."),
    NOT_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
