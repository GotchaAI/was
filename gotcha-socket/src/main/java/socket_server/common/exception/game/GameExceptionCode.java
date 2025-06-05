package socket_server.common.exception.game;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GameExceptionCode implements ExceptionCode {

    INVALID_GAME_ID(HttpStatus.BAD_REQUEST, "GAME-400-001", "유효하지 않은 게임 코드 입니다."),
    INVALID_EVENT_TYPE(HttpStatus.BAD_REQUEST, "GAME-400-002", "유효하지 않은 이벤트 타입입니다."),
    ALREADY_FINISHED_GAME(HttpStatus.BAD_REQUEST, "GAME-400-003", "이미 종료된 게임입니다."),
    INVALID_DRAWER_ID(HttpStatus.BAD_REQUEST, "GAME-400-004", "유효하지 않은 DRAWER-ID 입니다."),
    DRAWING_ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, "GAME-400-005", "이미 제출된 그림입니다."),
    INVALID_GAME_STATUS(HttpStatus.BAD_REQUEST, "GAME-400-006", "진행할 수 없는 이벤트입니다."),
    AI_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GAME-500-001", "AI 서버와의 연결 중에 오류가 발생하였습니다."),
    INVALID_GUESSER(HttpStatus.BAD_REQUEST, "GAME-400-007", "추측 제출 턴이 아닙니다.");


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
