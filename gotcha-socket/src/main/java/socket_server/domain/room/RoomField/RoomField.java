package socket_server.domain.room.RoomField;

import gotcha_common.exception.FieldValidationException;
import socket_server.domain.room.dto.RoomFieldUpdateReq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RoomField {
    TITLE("title"),
    PASSWORD("password"),
    HAS_PASSWORD("hasPassword"),
    DIFFICULTY("difficulty"),
    GAME_TYPE("gameType"),
    MIN("min"),
    MAX("max"),
    ROUND_COUNT("roundCount"),
    OWNER("owner"),
    OWNER_UUID("ownerUuid");

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

    public static void validateAll(List<RoomFieldUpdateReq> requests) {
        Map<String, String> errors = new HashMap<>();

        for (RoomFieldUpdateReq req : requests) {
            try {
                from(req.field());
            } catch (FieldValidationException e) {
                errors.put(req.field(), req.field() + " : 지원하지 않는 필드입니다");
            }
        }

        if (!errors.isEmpty()) {
            throw new FieldValidationException(errors);
        }
    }
}

