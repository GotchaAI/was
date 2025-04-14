package Gotcha.common.exception.exceptionCode;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GlobalExceptionCode implements ExceptionCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다. 서버 팀에 연락주세요."),
    FIELD_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "필드 검증 오류입니다."),
    CSRF_INVALID(HttpStatus.FORBIDDEN, "CSRF 토큰이 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 사용자를 찾을 수 없습니다.");

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