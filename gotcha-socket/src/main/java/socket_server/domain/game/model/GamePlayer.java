package socket_server.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 게임 참가자 데이터(Redis에 저장)
 */
@AllArgsConstructor
@Data
public class GamePlayer {
    private String playerUuid;
    private String nickname;
    private int score;
}

