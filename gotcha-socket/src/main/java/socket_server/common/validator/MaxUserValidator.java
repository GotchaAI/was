package socket_server.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.lobby.dto.CreateRoomReq;

public class MaxUserValidator implements ConstraintValidator<ValidMaxUser, CreateRoomReq> {

    @Override
    public boolean isValid(CreateRoomReq req, ConstraintValidatorContext context) {
        if (req == null || req.gameType() == null || req.maxUser() == null) {
            return true;
        }

        GameType gameMode = req.gameType();
        int maxUser = req.maxUser();

        boolean valid = maxUser >= gameMode.getMinPlayers() && maxUser <= gameMode.getMaxPlayers();

        if (!valid) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("maxUser")
                    .addConstraintViolation();
        }

        return valid;
    }

}
