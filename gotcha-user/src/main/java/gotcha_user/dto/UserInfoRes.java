package gotcha_user.dto;

import gotcha_domain.user.Role;
import gotcha_domain.user.User;
import gotcha_user.util.LevelExpProvider;

public record UserInfoRes(
        String nickname,
        String email,
        Role role,
        String uuid,
        int level,
        int expInLevel,
        int expToNextLevel,
        float expProgress
) {
    public static UserInfoRes fromEntity(User user){
        int exp = (int) user.getExp();
        int level = LevelExpProvider.getLevelByExp(exp);
        int currentLevelExpStart = LevelExpProvider.getCumulativeExp(level);
        int nextLevelExp = LevelExpProvider.getExpToNextLevel(exp);
        float progress = LevelExpProvider.getProgressInLevel(exp);

        return new UserInfoRes(
                user.getNickname(),
                user.getEmail(),
                user.getRole(),
                user.getUuid(),
                level,
                exp - currentLevelExpStart,
                nextLevelExp,
                progress
        );
    }
}
