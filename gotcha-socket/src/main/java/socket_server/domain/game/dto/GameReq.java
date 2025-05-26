package socket_server.domain.game.dto;

/**
 * 게임 이벤트 요청 들어올 때 클라이언트에서 넘겨줄 DTO
 */
public record GameReq(
        GameEventType gameEventType,
        Object data
) {
}
