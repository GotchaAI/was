package Gotcha.domain.friend.dto;

import jakarta.validation.constraints.NotNull;

public record FriendFollowingReq(
        @NotNull(message = "따라갈 상대방 유저의 uuid는 필수 요소입니다.")
        String followerUuid,
        @NotNull(message = "따라가기를 클릭한 유저의 uuid는 필수 요소입니다.")
        String followingUuid
) {
}
