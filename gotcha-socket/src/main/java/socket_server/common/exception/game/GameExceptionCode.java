package socket_server.common.exception.game;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GameExceptionCode implements ExceptionCode {

    INVALID_GAME_ID(HttpStatus.BAD_REQUEST, "GAME-400-001", "유효하지 않은 게임 코드 입니다."),
    INVALID_EVENT_TYPE(HttpStatus.BAD_REQUEST, "GAME-400-002", "유효하지 않은 이벤트 타입입니다."),
    ALREADY_FINISHED_GAME(HttpStatus.BAD_REQUEST, "GAME-400-003", "이미 종료된 게임입니다."),;


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
