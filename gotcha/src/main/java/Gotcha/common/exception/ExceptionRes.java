package Gotcha.common.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExceptionRes(
        String code,
        HttpStatus status,
        String message,
        Map<String, String> fields
) {
    public static ExceptionRes from(ExceptionCode error){
        return ExceptionRes.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    public static ExceptionRes from(ExceptionCode error, Map<String, String> fields) {
        return ExceptionRes.builder()
                .status(error.getStatus())
                .code(error.getCode())
                .message(error.getMessage())
                .fields(fields)
                .build();
    }
}
