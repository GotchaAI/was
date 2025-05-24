package socket_server.domain.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 게임 참가자 데이터(Redis에 저장)
 */
@AllArgsConstructor
@Getter
public class GamePlayer {
    private String playerUuid;
    private String nickname;
    @Setter
    private int score;
}

