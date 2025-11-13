package Gotcha.domain.auth.exception;

import gotcha_common.exception.ExceptionRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserAccountStatusExceptionHandler {

    @ExceptionHandler(UserAccountStatusException.class)
    public ResponseEntity<?> handleAccountStatusException(UserAccountStatusException e) {
        log.error("[Account Status Exception] {}: {}", e.getExceptionCode().getMessage(), e.getDetails());
        return ResponseEntity
                .status(e.getExceptionCode().getStatus())
                .body(ExceptionRes.from(e.getExceptionCode(), e.getDetails()));
    }

}
