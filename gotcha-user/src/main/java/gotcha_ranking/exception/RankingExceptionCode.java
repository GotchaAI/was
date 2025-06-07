package gotcha_ranking.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum RankingExceptionCode implements ExceptionCode {
    RANKING_NOT_FOUND(HttpStatus.NOT_FOUND, "RANK-404-001", "랭킹에 등록되지 않은 사용자입니다.");

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
