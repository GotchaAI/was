package socket_server.domain.room.handler;

import gotcha_common.exception.FieldValidationException;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.util.JsonSerializer;
import socket_server.common.validator.SocketFieldValidator;
import socket_server.domain.room.model.RoomEventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.dto.RoomUpdateReq;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

@Component
@RequiredArgsConstructor
public class UpdateRoomHandler implements RoomEventHandler{
    private final RoomService roomService;
    private final JsonSerializer jsonSerializer;
    private final SocketFieldValidator socketFieldValidator;

    @Override
    public RoomEventType getEventType() {
        return RoomEventType.UPDATE;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq roomReq) {
        RoomUpdateReq updateRequest = jsonSerializer.deserialize(roomReq.content(), RoomUpdateReq.class, getErrorType());
        socketFieldValidator.validateOrThrow(updateRequest, getErrorType());
        roomService.updateRoomField(roomId, updateRequest, userDetails.getUuid());
    }

}
