package socket_server.common.constants;

public interface WebSocketConstants {
    // 채팅 관련 채널
    String CHAT_PREFIX = "/sub/chat/";
    String CHAT_ALL_CHANNEL = CHAT_PREFIX + "all/";  // 전체 채팅
    String CHAT_PRIVATE_CHANNEL = CHAT_PREFIX + "private/";  // + 보낼 상대 닉네임 (1:1 채팅)
    String CHAT_ROOM_CHANNEL = CHAT_PREFIX + "room/";  // + 방 고유 ID (방 채팅)

    // 게임 관련 채널
    String GAME_PREFIX = "/sub/game/";
    String GAME_CHANNEL = GAME_PREFIX + "room/"; // + 방 고유 ID (게임 방)
    String GAME_READY_CHANNEL = GAME_CHANNEL + "ready/"; // + roomId
    String GAME_END_CHANNEL = GAME_CHANNEL + "end/"; // + roomId
    String GAME_INFO_CHANNEL = GAME_CHANNEL + "info/"; // + roomId
    String GAME_START_CHANNEL = GAME_CHANNEL + "start/"; // + roomId

    // 게임 종료 메시지
    String GAME_END_MESSAGE = "게임 종료";
}
