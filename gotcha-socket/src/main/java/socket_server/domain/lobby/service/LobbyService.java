package socket_server.domain.lobby.service;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.domain.lobby.dto.CreateRoomReq;
import socket_server.domain.lobby.dto.RoomIdRes;
import socket_server.domain.room.dto.RoomSummaryRes;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

import static socket_server.common.constants.WebSocketConstants.LOBBY_JOIN_CHANNEL;
import static socket_server.common.constants.WebSocketConstants.LOBBY_ROOM_CREATE_CHANNEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class LobbyService {
    private final RoomService roomService;
    private final RoomUserService roomUserService;
    private final LobbyBroadCaster lobbyBroadCaster;
    private final ErrorType LOBBY_ERROR = ErrorType.LOBBY;

    public void createRoom(CreateRoomReq request, SecurityUserDetails userDetails) {
        String uuid = userDetails.getUuid();
        roomUserService.checkUserNotInAnyRoom(uuid, LOBBY_ERROR);
        RoomMetadata roomMetadata = roomService.createRoom(request, userDetails);

        roomUserService.joinRoom(roomMetadata.getId(), uuid, userDetails.getNickname(), LOBBY_ERROR);
        //브로드캐스팅 -> 로비 소켓에 새로운 방 생성 전파
        RoomSummaryRes summary = RoomSummaryRes.of(roomMetadata, 1);
        lobbyBroadCaster.broadcastToRoomList("SYSTEM", RoomEventType.CREATE, summary);
        //개인채널 -> 방 생성이 잘 됨 전달
        lobbyBroadCaster.sendToUser(LOBBY_ROOM_CREATE_CHANNEL+uuid, uuid, new RoomIdRes(roomMetadata.getId()));
    }

    public void joinRoom(String roomId, SecurityUserDetails userDetails, String password) {
        String uuid = userDetails.getUuid();
        roomUserService.validateUserCanJoinRoom(roomId, userDetails.getUuid(), password, LOBBY_ERROR);
        roomUserService.joinRoom(roomId, uuid, userDetails.getNickname(), LOBBY_ERROR);

        //브로드캐스팅 -> 대기방 내 유저들(신규 유저 포함)에게 새로운 참가자 정보 전파
        roomUserService.broadcastUserInRoomInfo(roomId, uuid, LOBBY_ERROR);
        //개인채널 -> 방 참가 잘 됨 전달
        lobbyBroadCaster.sendToUser(LOBBY_JOIN_CHANNEL+roomId, uuid, new RoomIdRes(roomId));
    }

}
