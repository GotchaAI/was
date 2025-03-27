package Gotcha.domain.notification.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum NotificationExceptionCode implements ExceptionCode {

    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "필드 검증 오류입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공지사항입니다.");

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
