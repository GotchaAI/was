package Gotcha.domain.friend.dto;

import jakarta.validation.constraints.NotNull;

public record FriendReq(
        @NotNull(message = "친구 신청을 보낼 닉네임은 필수 요소입니다.")
        String nickname
) {
}
