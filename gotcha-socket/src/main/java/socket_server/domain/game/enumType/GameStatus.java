package socket_server.domain.game.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import socket_server.domain.game.model.Game;

@Getter
@AllArgsConstructor
public enum GameStatus {

    // 게임 시작
    GAME_STARTED("게임 시작됨"),

    // 라운드 진행 상태들
    ROUND_STARTED("라운드 시작됨"),
    DRAWING_PHASE("그리기 단계"),
    GUESSING_PHASE("추측 단계"),
    ROUND_ENDED("라운드 종료"),

    // 게임 종료
    GAME_ENDED("게임 종료");
    private String description;

    public boolean canHandleEvent(GameEventType gameEventType) {
        return switch(gameEventType){
            case ROUND_START -> this == GameStatus.GAME_STARTED || this == GameStatus.ROUND_ENDED;
            case DRAWING_SUBMIT, GUESS_START -> this == GameStatus.DRAWING_PHASE;
            case GUESS_SUBMIT, ROUND_END, GUESS_REQUEST, GUESS_RESULT, SCORE_UPDATE, BATTLE_END -> this == GameStatus.GUESSING_PHASE;
            case GAME_END -> this == GameStatus.ROUND_ENDED;
            case GAME_START -> this == GameStatus.GAME_ENDED;
            default -> false;
        };

    }

}
