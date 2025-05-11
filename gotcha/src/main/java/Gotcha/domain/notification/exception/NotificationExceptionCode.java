package Gotcha.domain.notification.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum NotificationExceptionCode implements ExceptionCode {

    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "NOTI-403-001", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI-404-001", "존재하지 않는 공지사항입니다.");

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
