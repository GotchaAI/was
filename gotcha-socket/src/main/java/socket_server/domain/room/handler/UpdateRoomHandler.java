package socket_server.domain.room.handler;

import gotcha_common.exception.FieldValidationException;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.dto.RoomReq;
import socket_server.domain.room.dto.RoomUpdateReq;
import socket_server.domain.room.service.RoomService;
import socket_server.domain.room.service.RoomUserService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UpdateRoomHandler implements RoomEventHandler{
    private final RoomUserService roomUserService;
    private final RoomService roomService;
    private final JsonSerializer jsonSerializer;
    private final Validator validator;

    @Override
    public EventType getEventType() {
        return EventType.UPDATE;
    }

    @Override
    public void handle(String roomId, SecurityUserDetails userDetails, RoomReq roomReq) {
        RoomUpdateReq updateRequest = jsonSerializer.deserialize(roomReq.content(), RoomUpdateReq.class);

        Set<ConstraintViolation<RoomUpdateReq>> violations = validator.validate(updateRequest);
        if (!violations.isEmpty()) {
            Map<String, String> fieldErrors = violations.stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));
            throw new FieldValidationException(fieldErrors);
        }

        roomService.updateRoomField(roomId, updateRequest, userDetails.getUuid());
    }

}
