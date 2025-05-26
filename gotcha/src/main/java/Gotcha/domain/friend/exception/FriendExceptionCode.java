package Gotcha.domain.friend.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum FriendExceptionCode  implements ExceptionCode {
    SELF_REQUEST_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FRIEND-400-001", "자기 자신에게는 친구 요청을 보낼 수 없습니다."),
    NOT_FRIEND(HttpStatus.BAD_REQUEST, "FRIEND-400-002", "해당 사용자와는 친구가 아닙니다."),
    FRIEND_REQUEST_ALREADY_EXISTS(HttpStatus.CONFLICT, "FRIEND-409-001", "이미 친구 요청을 보낸 상태입니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "FIREND-404-001", "친구 요청을 찾을 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.CONFLICT, "FRIEND-409-002", "이미 친구인 사용자입니다."),
    INVALID_REQUEST_ACCESS(HttpStatus.FORBIDDEN,"FRIEND-403-001", "해당 친구 요청에 접근 권한이 없습니다.");


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

