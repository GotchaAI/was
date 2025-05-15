package socket_server.domain.room.RoomField;

import gotcha_common.exception.FieldValidationException;

import java.util.Arrays;

public enum RoomField {
    TITLE("title"),
    PASSWORD("password"),
    HAS_PASSWORD("hasPassword"),
    AI_LEVEL("aiLevel"),
    GAME_MODE("gameMode"),
    MIN("min"),
    MAX("max"),
    OWNER("owner");

    private final String redisField;

    RoomField(String redisField) {
        this.redisField = redisField;
    }

    public String getRedisField() {
        return redisField;
    }

    public static RoomField from(String name) {
        return Arrays.stream(values())
                .filter(f -> f.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new FieldValidationException(
                        "field", name+" : 지원하지 않는 필드입니다: "
                ));
    }
}

