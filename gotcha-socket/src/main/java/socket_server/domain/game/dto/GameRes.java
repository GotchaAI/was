package socket_server.domain.game.dto;

import lombok.Builder;
import socket_server.domain.game.enumType.GameEventType;

import java.time.LocalDateTime;

/**
 * 게임 이벤트 발생 시 클라이언트로 보내줄 DTO
 */
@Builder
public record GameRes(
        GameEventType eventType,
        Object data,
        String aiSays,
        LocalDateTime eventAt
) {
}
