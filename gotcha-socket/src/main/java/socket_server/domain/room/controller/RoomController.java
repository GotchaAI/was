package socket_server.domain.room.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_user.service.UserService;
import gotcha_user.dto.UserInfoRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.config.RedisMessage;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

import static socket_server.common.constants.WebSocketConstants.*;

@Slf4j
@Controller
@MessageMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomUserService roomUserService;
    private final UserService userService;


    @MessageMapping("/create")
    public void createRoom(@Valid @Payload CreateRoomRequest request, @AuthenticationPrincipal SecurityUserDetails userDetails) {
        String userId = userDetails.getUsername();

        roomUserService.checkUserNotInAnyRoom(userId);
        RoomMetadata metadata = roomService.createRoom(request, userId);

        roomService.broadcastRoomInfo(userId, metadata);
    }

    @MessageMapping("/join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, @AuthenticationPrincipal SecurityUserDetails userDetails) throws JsonProcessingException {
        String userId = userDetails.getUsername();

        UserInfoRes userInfoRes = userService.getUserInfo(userDetails);

        RoomUserInfo roomUserInfo = RoomUserInfo.fromDTO(userDetails.getUuid(), userInfoRes);

        // room에 들어오면
        roomUserService.joinRoom(roomUserInfo, roomId);

        // 그 방에 있는 새로운 userList broadcast
        roomUserService.broadcastUserList(roomId, userId);

    }

/*    @MessageMapping("/ready/{roomId}")
    public void ready(@DestinationVariable String roomId, @AuthenticationPrincipal SecurityUserDetails userDetails) throws JsonProcessingException {
        String userId = userDetails.getUsername();

        roomUserService.ready(userId, roomId);


    }*/



}
