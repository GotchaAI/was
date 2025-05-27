package socket_server.domain.game.dto;

import socket_server.domain.game.enumType.GameEventType;

import java.time.LocalDateTime;

/**
 * 게임 이벤트 발생 시 클라이언트로 보내줄 DTO
 */
public record GameRes(
        GameEventType eventType,
        Object data,
        LocalDateTime eventAt
) {
}
