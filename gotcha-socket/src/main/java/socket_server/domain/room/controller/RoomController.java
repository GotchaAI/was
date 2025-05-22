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
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;


@Slf4j
@Controller
@MessageMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomUserService roomUserService;

    @MessageMapping("/create")
    public void createRoom(@Valid @Payload CreateRoomRequest request, @AuthenticationPrincipal SecurityUserDetails userDetails) {
        String userId = userDetails.getUuid();
        roomUserService.checkUserNotInAnyRoom(userId);
        RoomMetadata metadata = roomService.createRoom(request, userId);
        roomService.broadcastRoomInfo(userId, metadata);
    }

    @MessageMapping("/join/{roomId}")
    public void joinRoom(@DestinationVariable String roomId, @AuthenticationPrincipal SecurityUserDetails userDetails){
        String userId = userDetails.getUuid();
        RoomUserInfo roomUserInfo = RoomUserInfo.builder().
                userId(userId).
                nickname(userDetails.getNickname()).
                ready(false).
                build();

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
