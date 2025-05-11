package gotcha_auth.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum JwtExceptionCode implements ExceptionCode {
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT-401-001", "Access Token이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT-401-002", "Refresh Token이 만료되었습니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT-401-003", "Access Token의 서명이 잘못되었습니다."),
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT-401-004", "Access Token을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT-401-005", "Refresh Token을 찾을 수 없습니다."),
    BLACKLIST_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-401-006", "접근 불가한 AccessToken입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "JWT-401-007", "잘못된 Access Token입니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "JWT-401-008", "알 수 없는 토큰 에러입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "JWT-403-001", "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
