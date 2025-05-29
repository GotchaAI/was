package gotcha_user.exceptionCode;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@ToString
public enum UserExceptionCode implements ExceptionCode {

    NICKNAME_EXIST(HttpStatus.CONFLICT, "USER-409-001", "이미 존재하는 닉네임입니다."),
    EMAIL_EXIST(HttpStatus.CONFLICT, "USER-409-002", "이미 가입된 이메일입니다."),
    SAME_NICKNAME(HttpStatus.CONFLICT, "USER-409-003", "현재 닉네임과 동일합니다."),
    INVALID_USERID(HttpStatus.NOT_FOUND, "USER-404-001", "존재하지 않는 사용자입니다."),
    NICKNAME_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "USER-400-001", "닉네임 중복 확인이 되지 않았습니다.");

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
