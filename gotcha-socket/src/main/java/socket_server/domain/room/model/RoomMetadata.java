package socket_server.domain.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;

import java.util.Map;

@Component
@Getter
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
        metadata.difficulty = Difficulty.valueOf((String) map.getOrDefault("difficulty", "BASIC"));
        metadata.gameType =  GameType.valueOf((String) map.getOrDefault("gameType", "TRICK_MYOMYO"));
        metadata.ownerUuid = (String) map.getOrDefault("ownerUuid", "");
        return metadata;
    }
}
