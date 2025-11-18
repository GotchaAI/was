package socket_server.common.util;

import gotcha_domain.chat.ChatType;
import gotcha_domain.user.ChatOption;
import gotcha_domain.user.PrivateChatOption;

public final class ChatPermissionUtil {

    public ChatPermissionUtil() {};

    public static boolean canReceiveMessageFromCache(
            ChatOption chatOption,
            PrivateChatOption privateChatOption,
            boolean isFriend,
            ChatType chatType
    ) {
        //전체 차단 설정이면 무조건 불가
        if (chatOption == ChatOption.DENY_ALL) {
            return false;
        }

        //친구만 허용인데 친구가 아니면 불가
        if (chatOption == ChatOption.FRIENDS_ONLY && !isFriend) {
            return false;
        }

        //귓속말 차단 여부
        if (chatType == ChatType.PRIVATE && privateChatOption == PrivateChatOption.DENY) {
            return false;
        }

        //위 조건을 모두 통과하면 수신 가능
        return true;
    }
}
