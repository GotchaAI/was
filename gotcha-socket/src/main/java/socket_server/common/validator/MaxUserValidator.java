package socket_server.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.room.dto.CreateRoomRequest;

public class MaxUserValidator implements ConstraintValidator<ValidMaxUser, CreateRoomRequest> {

    @Override
    public boolean isValid(CreateRoomRequest req, ConstraintValidatorContext context) {
//        if (req == null || req.gameMode() == null || req.maxUser() == null) {
            return true;
//        }
//
//        GameMode gameMode = req.gameMode();
//        int maxUser = req.maxUser();
//
//        return maxUser >= gameMode.getMinPlayers() && maxUser <= gameMode.getMaxPlayers();
    }

}
