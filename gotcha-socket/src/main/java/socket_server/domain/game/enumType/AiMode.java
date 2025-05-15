package socket_server.domain.game.enumType;

public enum AiMode {
    BASIC("기본", "1"),
    ADVANCED("고급", "2");

    private final String description;
    private final String level;

    AiMode(String description, String level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficultyLevel() {
        return level;
    }
}
