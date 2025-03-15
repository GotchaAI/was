package Gotcha.common.jwt.exception;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public enum JwtExceptionCode implements ExceptionCode {
    ACCESS_TOKEN_EXPIRED(401, "Access Token아 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, "Refresh Token이 만료되었습니다."),
    ACCESS_TOKEN_NOT_FOUND(404, "Access Token을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "Refresh Token을 찾을 수 없습니다."),
    BLACKLIST_ACCESS_TOKEN(401, "접근 불가한 AccessToken입니다."),
    INVALID_ACCESS_TOKEN(401, "잘못된 Access Token입니다.");


    private final int status;
    private final String message;


    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}