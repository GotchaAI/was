package socket_server.domain.lobby.dto;

import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;

import java.util.List;

public record RoomJoinRes(
        RoomMetadata roomMetadata,
        List<RoomUserInfo> userInfos
) {
}
