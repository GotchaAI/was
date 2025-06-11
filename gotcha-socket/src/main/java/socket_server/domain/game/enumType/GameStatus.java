package socket_server.domain.game.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameStatus {

    // 게임 시작
    GAME_STARTED("게임 시작됨"),

    // 라운드 진행 상태들
    ROUND_STARTED("라운드 시작됨"),
    DRAWING_PHASE("그리기 단계"),
    GUESSING_STARTED("추측 시작"),
    GUESSING_REQUESTED("추측 기다리는 중"),
    GUESSING_PROCESSING("추측 처리 중"),
    GUESSING_ENDED("추측 종료"),
    ROUND_ENDED("라운드 종료"),

    // 게임 종료
    GAME_ENDED("게임 종료"),


    // 연결 끊김
    DISCONNECTED("연결 끊김"),;


    private final String description;

    /**
     * GAME_STARTED: 게임 시작
     *
     */
    public boolean canHandleEvent(GameEventType gameEventType) {
        return switch(gameEventType){
            case ROUND_START -> this == GameStatus.GAME_STARTED || this == GameStatus.ROUND_ENDED;
            case GUESS_START -> this == GameStatus.DRAWING_PHASE || this == GameStatus.GUESSING_ENDED;
            case DRAWING_SUBMIT -> this == GameStatus.DRAWING_PHASE;
            case GUESS_RESULT,  BATTLE_END -> this == GameStatus.GUESSING_PROCESSING;
            case ROUND_END -> this == GameStatus.GUESSING_ENDED;
            case GUESS_REQUEST -> this == GameStatus.GUESSING_STARTED || this == GameStatus.GUESSING_PROCESSING;
            case GUESS_SUBMIT -> this == GameStatus.GUESSING_REQUESTED;
            case GAME_END -> this == GameStatus.ROUND_ENDED || this == GameStatus.DISCONNECTED;
            case GAME_START, SCORE_UPDATE -> this == GameStatus.GAME_ENDED;
            default -> false;
        };

    }

}
