package gotcha_user.dto;

import gotcha_domain.user.ChatOption;
import gotcha_domain.user.PrivateChatOption;
import gotcha_domain.user.User;

public record UserChatSettingRes(
        ChatOption chatOption,
        PrivateChatOption privateChatOption
) {
    public static UserChatSettingRes fromUser(User user){
        return new UserChatSettingRes(
                user.getChatOption(),
                user.getPrivateChatOption()
        );
    }
}
