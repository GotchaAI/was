package Gotcha.common.jwt.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum JwtExceptionCode implements ExceptionCode {
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token아 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Access Token을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh Token을 찾을 수 없습니다."),
    BLACKLIST_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "접근 불가한 AccessToken입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 Access Token입니다.");


    private final HttpStatus status;
    private final String message;


    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}