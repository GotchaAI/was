package Gotcha.common.exception.exceptionCode;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
    String getCode();
    HttpStatus getStatus();
    String getMessage();
}
