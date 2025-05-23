package socket_server.domain.room.controller;

import gotcha_common.exception.CustomException;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.exception.room.RoomExceptionCode;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.model.RoomMetadata;
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
        String userUuid = userDetails.getUuid();
        roomUserService.checkUserNotInAnyRoom(userUuid);
        RoomMetadata metadata = roomService.createRoom(request, userUuid);
        roomService.broadcastRoomInfo(userUuid, metadata);
    }

    @MessageMapping("/{roomId}")
    public void room(@DestinationVariable String roomId,
                     @Valid @Payload RoomReq request,
                     @AuthenticationPrincipal SecurityUserDetails userDetails) {
        switch (request.eventType()) {
            case JOIN -> joinRoom(roomId, userDetails.getUuid(), userDetails.getNickname());
            default -> throw new CustomException(RoomExceptionCode.INVALID_EVENT_TYPE);
        }
    }

    public void joinRoom(String roomId, String userUuid, String nickname){
        // room에 들어오면
        roomUserService.joinRoom(roomId, userUuid, nickname);

        // 그 방에 있는 새로운 userList broadcast
        roomUserService.broadcastUserList(roomId, userUuid);
    }
}
