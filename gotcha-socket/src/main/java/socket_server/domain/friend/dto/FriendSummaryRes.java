package socket_server.domain.friend.dto;

import gotcha_domain.friend.FriendRequest;

public record FriendSummaryRes(
        Long id,
        String nickname,
        String uuid
) {
    public static FriendSummaryRes from(FriendRequest friendRequest) {
        return new FriendSummaryRes(
                friendRequest.getId(),
                friendRequest.getFromUser().getNickname(),
                friendRequest.getFromUser().getUuid()
        );
    }
}
