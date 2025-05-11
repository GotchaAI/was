package gotcha_common.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<?> handleCustomException(final CustomException e) {
        ExceptionCode error = e.getExceptionCode();
        log.error("[Custom Exception] {}", error.getMessage());
        return ResponseEntity.status(error.getStatus()).body(ExceptionRes.from(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleValidationException(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for(FieldError fieldError : e.getBindingResult().getFieldErrors() ){
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            errors.put(field, message);

            log.error("[Validation Exception] {}: {}", field, message);
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(final Exception e) {
        log.error("[Exception] {}", e.getMessage());
        ExceptionCode error = GlobalExceptionCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(error.getStatus()).body(ExceptionRes.from(error));
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<?> handleFieldValidationException(FieldValidationException e) {
        e.getFieldErrors().forEach((field, message) ->
                log.warn("[Field Validation Exception] {}: {}", field, message)
        );
        ExceptionCode error = GlobalExceptionCode.FIELD_VALIDATION_ERROR;
        return ResponseEntity.status(error.getStatus()).body(ExceptionRes.from(error, e.getFieldErrors()));
    }

}