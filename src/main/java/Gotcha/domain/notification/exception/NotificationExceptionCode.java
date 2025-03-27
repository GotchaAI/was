package Gotcha.domain.notification.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum NotificationExceptionCode implements ExceptionCode {
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
