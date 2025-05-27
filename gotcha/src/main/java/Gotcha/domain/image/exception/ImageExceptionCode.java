package Gotcha.domain.image.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ImageExceptionCode implements ExceptionCode {
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "IMAGE-400-001", "이미지 타입이 유효하지 않습니다.(png, jpg, jpeg만 허용)");

    private final HttpStatus status;
    private final String code;
    private final String message;


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
