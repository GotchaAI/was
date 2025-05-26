package Gotcha.domain.friend.dto;

import gotcha_domain.user.User;

import java.time.LocalDateTime;

public record FriendRes(
        String nickname,
        String uuid,
        LocalDateTime lastLogout
) {
    public static FriendRes from(User user) {
        return new FriendRes(
                user.getNickname(),
                user.getUuid(),
                user.getLastLogout()
        );
    }
}
