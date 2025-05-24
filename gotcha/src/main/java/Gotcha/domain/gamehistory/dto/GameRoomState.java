package Gotcha.domain.gamehistory.dto;

public enum GameRoomState {
    WAITING("대기 중"),
    PLAYING("진행 중"),
    ENDED("종료됨");

    private final String description;

    GameRoomState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
