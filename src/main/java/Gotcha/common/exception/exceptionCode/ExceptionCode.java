package Gotcha.common.exception.exceptionCode;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {
    HttpStatus getStatus();
    String getMessage();
}
