package Gotcha.domain.mypage.dto;

import gotcha_domain.user.ChatOption;
import gotcha_domain.user.PrivateChatOption;
import jakarta.validation.constraints.NotNull;

public record ChatSettingReq(
        @NotNull(message = "chatOption은 필수입니다.")
        ChatOption chatOption,
        @NotNull(message = "privateChatOption은 필수입니다.")
        PrivateChatOption privateChatOption
) {
}
