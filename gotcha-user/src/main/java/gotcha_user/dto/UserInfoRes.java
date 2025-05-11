package gotcha_user.dto;

import gotcha_domain.user.Role;
import gotcha_domain.user.User;

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
