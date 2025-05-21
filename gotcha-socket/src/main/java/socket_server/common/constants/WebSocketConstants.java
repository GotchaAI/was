package socket_server.common.constants;

public interface WebSocketConstants {
    // 채팅 관련 채널
    String CHAT_PREFIX = "/sub/chat/";
    String CHAT_ALL_CHANNEL = CHAT_PREFIX + "all";  // 전체 채팅
    String CHAT_PRIVATE_CHANNEL = CHAT_PREFIX + "private/";  // + 보낼 상대 닉네임 (1:1 채팅)
    String CHAT_ROOM_CHANNEL = CHAT_PREFIX + "room/";  // + 방 고유 ID (방 채팅)

    //대기방 관련 채널
    String ROOM_PREFIX = "/sub/room/";
    String ROOM_LIST_INFO = ROOM_PREFIX + "list/info";
    String ROOM_JOIN = ROOM_PREFIX+"join/"; // + roomId
    String ROOM_LEAVE = ROOM_PREFIX+"leave/"; // + roomId
    String ROOM_CREATE_INFO = ROOM_PREFIX + "create/info";
    String ROOM_UPDATE = ROOM_PREFIX + "update/"; // + roomId

    // 게임 관련 채널
    String GAME_PREFIX = "/sub/game/";
    String GAME_READY_CHANNEL = GAME_PREFIX + "ready/"; // + roomId
    String GAME_END_CHANNEL = GAME_PREFIX + "end/"; // + roomId
    String GAME_INFO_CHANNEL = GAME_PREFIX + "info/"; // + roomId
    String GAME_START_CHANNEL = GAME_PREFIX     + "start/"; // + roomId

    // 개인 유저 관련 채널
    String PERSONAL_PREFIX = "/sub/personal/";
    String PERSONAL_ROOM_CREATE_RESPONSE =  PERSONAL_PREFIX +"room/create/"; //+userId

    // 에러 처리 채널
    String ERROR_CHANNEL_PREFIX = "/user/";
    String ERROR_CHANEL = "/queue/errors";
}
