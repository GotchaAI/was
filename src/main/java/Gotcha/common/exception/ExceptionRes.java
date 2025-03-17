package Gotcha.common.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import org.springframework.http.HttpStatus;
import lombok.Builder;

@Builder
public record ExceptionRes(
        HttpStatus status,
        String message
) {
    public static ExceptionRes from(ExceptionCode error){
        return ExceptionRes.builder()
                .status(error.getStatus())
                .message(error.getMessage())
                .build();
    }
}
