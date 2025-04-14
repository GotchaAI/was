package Gotcha.domain.inquiry.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum InquiryExceptionCode implements ExceptionCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 QnA 입니다.");

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
