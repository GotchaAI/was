package Gotcha.domain.inquiry.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum InquiryExceptionCode implements ExceptionCode {

    INVALID_INQUIRYID(HttpStatus.NOT_FOUND, "QNA-404-001", "존재하지 않는 QnA 입니다."),
    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "QNA-403-001","권한이 없습니다."),
    ALREADY_SOLVED(HttpStatus.CONFLICT, "QNA-409-001", "이미 답변이 작성되었습니다.");

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
