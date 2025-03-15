package Gotcha.domain.auth.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserExceptionCode implements ExceptionCode {

    INVALID_USERNAME_AND_PASSWORD(404, "아이디 또는 비밀번호가 유효하지 않습니다."),
    INVALID_USERID(404,"존재하지 않는 사용자입니다.");

    private final int status;
    private final String message;

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
