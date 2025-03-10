package Gotcha.domain.game.validator;

import Gotcha.domain.game.dto.GameRoom;
import Gotcha.domain.game.entity.GameType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxUserValidator implements ConstraintValidator<ValidMaxUser, GameRoom> {
    @Override
    public boolean isValid(GameRoom gameRoom, ConstraintValidatorContext context) {
        if (gameRoom == null || gameRoom.gameType() == null || gameRoom.maxUser() == null) {
            return true;
        }

        int maxUser = gameRoom.maxUser();
        GameType gameType = gameRoom.gameType();

        return switch (gameType) {
            case 속여라 -> maxUser == 2;
            case 이어그리기 -> maxUser >= 4 && maxUser <= 8;
            default -> false;
        };
    }
}
