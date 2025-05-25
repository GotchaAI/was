package socket_server.domain.room.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

}
