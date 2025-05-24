package socket_server.common.exception.game;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GameExceptionCode implements ExceptionCode {

    INVALID_GAME_ID(HttpStatus.BAD_REQUEST, "GAME_400_001", "유효하지 않은 고유 방 코드 입니다."),
    GAME_ID_EXHAUSTED(HttpStatus.SERVICE_UNAVAILABLE, "GLOBAL-503-001", "사용 가능한 게임 코드가 모두 소진되었습니다. 서버 팀에 문의해주세요.");

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
