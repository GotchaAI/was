package socket_server.domain.room.handler;

import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomFieldUpdateReq;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateRoomHandler implements RoomEventHandler{
    private final RoomUserService roomUserService;
    private final RoomService roomService;
    private final JsonSerializer jsonSerializer;

    @Override
    public EventType getEventType() {
        return EventType.UPDATE;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq roomReq) {
        List<RoomFieldUpdateReq> updateRequests = jsonSerializer.deserializeList(roomReq.content(), RoomFieldUpdateReq.class);
        roomUserService.validateRoomHost(roomId, userDetails.getUuid());
        roomService.updateRoomField(roomId, updateRequests);
    }

}
