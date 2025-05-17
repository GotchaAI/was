package socket_server.domain.room.controller;

import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import socket_server.common.config.RedisMessage;
import socket_server.domain.room.dto.CreateRoomRequest;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

import static socket_server.common.constants.WebSocketConstants.PERSONAL_ROOM_CREATE_RESPONSE;
import static socket_server.common.constants.WebSocketConstants.ROOM_CREATE_INFO;

@Slf4j
@Controller
@MessageMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final RoomUserService roomUserService;
    private final RedisTemplate<String, Object> objectRedisTemplate;

    @MessageMapping("/create")
    public void createRoom(@Valid @Payload CreateRoomRequest request, @AuthenticationPrincipal SecurityUserDetails userDetails) {
        String userId = userDetails.getUsername();

        roomUserService.checkUserNotInAnyRoom(userId);
        RoomMetadata metadata = roomService.createRoom(request, userId);

        objectRedisTemplate.convertAndSend(ROOM_CREATE_INFO,
                new RedisMessage(userId, ROOM_CREATE_INFO, metadata)); //방 목록 생성 브로드 캐스트 용
        objectRedisTemplate.convertAndSend(PERSONAL_ROOM_CREATE_RESPONSE,
                new RedisMessage(userId, PERSONAL_ROOM_CREATE_RESPONSE, metadata)); //본인의 대기방 생성 확인 용
    }

}
