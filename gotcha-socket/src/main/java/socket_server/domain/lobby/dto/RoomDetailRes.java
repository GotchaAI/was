package socket_server.domain.lobby.dto;

import socket_server.domain.room.dto.RoomInfoRes;
import socket_server.domain.room.model.RoomUserInfo;

import java.util.List;

public record RoomDetailRes(
        RoomInfoRes roomInfo,
        List<RoomUserInfo> userInfos
) {
}
