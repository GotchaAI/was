package socket_server.domain.room.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import socket_server.domain.game.model.GamePlayer;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomUserInfo {

    private String userUuid;
    private String nickname;
    @Setter
    private boolean ready;

    static public GamePlayer toGamePlayer(RoomUserInfo roomUserInfo) {
        return new GamePlayer(roomUserInfo.getUserUuid(), roomUserInfo.getNickname(), 0);
    }

}
