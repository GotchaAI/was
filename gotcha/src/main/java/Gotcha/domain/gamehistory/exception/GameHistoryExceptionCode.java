package Gotcha.domain.gamehistory.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum GameHistoryExceptionCode implements ExceptionCode {

    GAME_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME-404-001", "게임 정보가 존재하지 않습니다.");

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
