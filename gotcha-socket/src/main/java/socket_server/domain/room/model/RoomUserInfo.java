package socket_server.domain.room.model;

import gotcha_user.dto.UserInfoRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomUserInfo {

    private String userId;
    private String nickname;
    private boolean ready;


    public static RoomUserInfo fromDTO(String userId, UserInfoRes userInfoRes){
        RoomUserInfo roomUserInfo = new RoomUserInfo();
        roomUserInfo.userId = userId;
        roomUserInfo.nickname = userInfoRes.nickname();
        roomUserInfo.ready = false;
        return roomUserInfo;
    }

}
