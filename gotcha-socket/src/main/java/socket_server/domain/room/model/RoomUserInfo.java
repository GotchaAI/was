package socket_server.domain.room.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomUserInfo {

    private String userUuid;
    private String nickname;
    private boolean ready;

}
