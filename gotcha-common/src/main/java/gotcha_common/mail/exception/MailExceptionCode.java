package gotcha_common.mail.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MailExceptionCode implements ExceptionCode {
    ALREADY_MAIL_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "MAIL-429-001", "이미 메일을 요청하셨습니다."),
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "MAIL-400-001", "인증번호가 만료되었습니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "MAIL-400-002", "인증번호가 일치하지 않습니다.");

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
