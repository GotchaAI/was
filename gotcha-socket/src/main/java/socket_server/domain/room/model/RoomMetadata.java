package socket_server.domain.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

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
    private String aiLevel;
    private String gameMode;
    private String uuid;


    public static RoomMetadata fromRedisMap(String id, Map<Object, Object> map) {
        RoomMetadata metadata = new RoomMetadata();
        metadata.id = id;
        metadata.title = (String) map.getOrDefault("title", "");
        metadata.owner = (String) map.getOrDefault("owner", "");
        metadata.hasPassword = Boolean.parseBoolean((String) map.getOrDefault("hasPassword", "false"));
        metadata.password = (String) map.getOrDefault("password", "");
        metadata.max = Integer.parseInt((String) map.getOrDefault("max", "0"));
        metadata.min = Integer.parseInt((String) map.getOrDefault("min", "0"));
        metadata.aiLevel = (String) map.getOrDefault("aiLevel", "NORMAL");
        metadata.gameMode = (String) map.getOrDefault("gameMode", "STANDARD");
        metadata.uuid = (String) map.getOrDefault("uuid", "");
        return metadata;
    }
}
