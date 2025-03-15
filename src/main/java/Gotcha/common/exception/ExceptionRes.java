package Gotcha.common.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.Builder;

@Builder
public record ExceptionRes(
        int status,
        String message
) {
    public static ExceptionRes from(ExceptionCode error){
        return ExceptionRes.builder()
                .status(error.getStatus())
                .message(error.getMessage())
                .build();
    }
}
