package gotcha_common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFormatException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ex = (InvalidFormatException) e.getCause();
            HttpStatus status = HttpStatus.BAD_REQUEST;

            String fieldName = ex.getPath().stream()
                    .map(p -> p.getFieldName())
                    .collect(Collectors.joining("."));

            String invalidValue = ex.getValue().toString();

            String allowedValues = Arrays.stream(ex.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String errorMessage = String.format(
                    "필드 '%s'에 잘못된 값 '%s'이(가) 입력되었습니다. 허용되는 값: [%s]",
                    fieldName, invalidValue, allowedValues
            );

            Map<String, Object> errorBody = Map.of(
                    "status", status.value(),
                    "code", "INVALID_INPUT_VALUE",
                    "message", errorMessage,
                    "field", fieldName
            );

            return new ResponseEntity<>(errorBody, status);
        }

        // Enum 변환 오류가 아닌 다른 HttpMessageNotReadableException의 경우
        Map<String, Object> errorBody = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "code", "INVALID_REQUEST_BODY",
                "message", "요청 본문의 형식이 올바르지 않습니다."
        );
        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
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
        return ResponseEntity.status(error.getStatus()).body(ExceptionRes.from(error, new HashMap<>(e.getFieldErrors())));
    }

}