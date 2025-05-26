package socket_server.domain.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomMetadata {
    private String id;
    private String title;
    private String owner;
    private boolean hasPassword;
    private String password;
    private int max;
    private int min;
    private Difficulty difficulty;
    private GameType gameType;
    private int roundCount;
    private String ownerUuid;


    public static RoomMetadata fromRedisMap(String id, Map<Object, Object> map) {
        RoomMetadata metadata = new RoomMetadata();
        metadata.id = id;
        metadata.title = (String) map.getOrDefault("title", "");
        metadata.owner = (String) map.getOrDefault("owner", "");
        metadata.hasPassword = Boolean.parseBoolean((String) map.getOrDefault("hasPassword", "false"));
        metadata.password = (String) map.getOrDefault("password", "");
        metadata.max = Integer.parseInt((String) map.getOrDefault("max", "0"));
        metadata.min = Integer.parseInt((String) map.getOrDefault("min", "0"));
        metadata.roundCount = Integer.parseInt((String) map.getOrDefault("roundCount", "1"));
        metadata.difficulty = Difficulty.valueOf((String) map.getOrDefault("difficulty", "BASIC"));
        metadata.gameType =  GameType.valueOf((String) map.getOrDefault("gameType", "TRICK_MYOMYO"));
        metadata.ownerUuid = (String) map.getOrDefault("ownerUuid", "");
        return metadata;
    }

    public Map<String, String> toRedisMap() {
        Map<String, String> map = new HashMap<>();
        map.put("title", this.title);
        map.put("owner", this.owner);
        map.put("hasPassword", String.valueOf(this.hasPassword));
        map.put("password", this.password);
        map.put("max", String.valueOf(this.max));
        map.put("min", String.valueOf(this.min));
        map.put("roundCount", String.valueOf(this.roundCount));
        map.put("difficulty", this.difficulty.name());
        map.put("gameType", this.gameType.name());
        map.put("ownerUuid", this.ownerUuid);
        return map;
    }
}
