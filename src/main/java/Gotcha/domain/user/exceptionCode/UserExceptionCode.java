package Gotcha.domain.user.exceptionCode;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum UserExceptionCode implements ExceptionCode {
    NICKNAME_EXIST(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    EMAIL_EXIST(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),

    INVALID_ID(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

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
