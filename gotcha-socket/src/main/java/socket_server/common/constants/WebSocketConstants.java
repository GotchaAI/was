package socket_server.common.constants;

public interface WebSocketConstants {
    // 채팅 관련 채널
    String CHAT_PREFIX = "/sub/chat/";
    String CHAT_ALL_CHANNEL = CHAT_PREFIX + "all";  // 전체 채팅
    String CHAT_PRIVATE_CHANNEL = CHAT_PREFIX + "private/";  // + 보낼 상대 닉네임 (1:1 채팅)
    String CHAT_ROOM_CHANNEL = CHAT_PREFIX + "room/";  // + 방 고유 ID (방 채팅)

    //대기방 관련 채널
    String ROOM_PREFIX = "/sub/room/";

    // 게임 관련 채널
    String GAME_PREFIX = "/sub/game/";
    String GAME_READY_CHANNEL = GAME_PREFIX + "ready/"; // + roomId
    String GAME_END_CHANNEL = GAME_PREFIX + "end/"; // + roomId
    String GAME_INFO_CHANNEL = GAME_PREFIX + "info/"; // + roomId
    String GAME_START_CHANNEL = GAME_PREFIX + "start/"; // + roomId

    String LOBBY_PREFIX = "/sub/lobby/";
    String LOBBY_JOIN_CHANNEL = LOBBY_PREFIX+"join/"; // + roomId
    String LOBBY_ROOM_CREATE_CHANNEL = LOBBY_PREFIX+"create/"; // + uuid
    String LOBBY_ROOM_LIST_EVENT = LOBBY_PREFIX + "list/event";

    // 에러 처리 채널
    String ERROR_CHANNEL_PREFIX = "/user/";
    String ERROR_DEFAULT_CHANEL = "/queue/errors";
    String ERROR_ROOM_CHANNEL = "/room/errors";
    String ERROR_LOBBY_CHANNEL = "/lobby/errors";
    String ERROR_CHAT_CHANNEL = "/chat/errors";
    String ERROR_GAME_CHANNEL = "/game/errors";
}
