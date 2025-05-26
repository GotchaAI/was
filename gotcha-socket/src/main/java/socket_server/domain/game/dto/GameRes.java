package socket_server.domain.game.dto;

import java.time.LocalDateTime;

/**
 * 게임 이벤트 발생 시 클라이언트로 보내줄 DTO
 */
public record GameRes(
        GameEventType eventType,
        AISaysRes data,
        LocalDateTime eventAt
) {
}
