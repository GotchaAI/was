package socket_server.domain.game.enumType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GameStatus {
    // 게임 시작 전
    WAITING("대기 중"),

    // 게임 시작
    GAME_STARTED("게임 시작됨"),

    // 라운드 진행 상태들
    ROUND_STARTED("라운드 시작됨"),
    DRAWING_PHASE("그리기 단계"),
    DRAWING_SUBMITTED("그림 제출 완료"),
    GUESSING_PHASE("추측 단계"),
    ROUND_ENDED("라운드 종료"),

    // 게임 종료
    GAME_ENDED("게임 종료");
    private String description;

    public String getDescription(){
        return description;
    }

    public boolean canHandleEvent(GameEventType gameEventType) {
        return switch (this) {
            case WAITING, GAME_STARTED, ROUND_ENDED -> gameEventType == GameEventType.ROUND_START;
            case ROUND_STARTED, DRAWING_PHASE -> gameEventType == GameEventType.DRAWING_SUBMIT;
            case DRAWING_SUBMITTED -> gameEventType == GameEventType.GUESS_REQUEST;
            case GUESSING_PHASE -> gameEventType == GameEventType.GUESS_SUBMIT ||
                    gameEventType == GameEventType.GUESS_RESULT ||
                    gameEventType == GameEventType.SCORE_UPDATE ||
                    gameEventType == GameEventType.ROUND_END;
            default -> false;
        };
    }

}
