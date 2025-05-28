package socket_server.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import socket_server.domain.room.dto.CreateRoomRequest;

public class PasswordValidator implements ConstraintValidator<ValidPassword, CreateRoomRequest> {
    @Override
    public boolean isValid(CreateRoomRequest request, ConstraintValidatorContext context) {
        if (request.hasPassword()) {
            String password = request.password();

            // 4자리 숫자 정규식 검사
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
