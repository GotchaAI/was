package socket_server.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import socket_server.domain.lobby.dto.CreateRoomReq;
import socket_server.domain.room.dto.RoomUpdateReq;

public class PasswordValidator implements ConstraintValidator<ValidPassword, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof CreateRoomReq request) {
            return validatePassword(request.hasPassword(), request.password(), context);
        }
        if (obj instanceof RoomUpdateReq request) {
            return validatePassword(request.hasPassword(), request.password(), context);
        }
        // 지원하지 않는 타입은 기본 true (검증 통과)
        return true;
    }

    private boolean validatePassword(boolean hasPassword, String password, ConstraintValidatorContext context) {
        if (hasPassword) {
            if (password == null || !password.matches("^[0-9]{4}$")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("비밀번호는 4자리 숫자로 입력해야 합니다.")
                        .addPropertyNode("password")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}

