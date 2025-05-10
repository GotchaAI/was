package Gotcha.domain.user.dto;

import Gotcha.domain.user.entity.Role;
import Gotcha.domain.user.entity.User;

public record UserInfoRes(
        String nickname,
        String email,
        Role role
) {
    public static UserInfoRes fromEntity(User user){
        return new UserInfoRes(
                user.getNickname(),
                user.getEmail(),
                user.getRole()
        );
    }
}
