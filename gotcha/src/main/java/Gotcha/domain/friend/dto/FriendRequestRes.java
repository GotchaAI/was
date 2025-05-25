package Gotcha.domain.friend.dto;

import gotcha_domain.friend.FriendRequest;

public record FriendRequestRes(
        Long id,
        String nickname,
        String uuid
) {
    public static FriendRequestRes from(FriendRequest friendRequest) {
        return new FriendRequestRes(
                friendRequest.getId(),
                friendRequest.getFromUser().getNickname(),
                friendRequest.getFromUser().getUuid()
        );
    }
}
