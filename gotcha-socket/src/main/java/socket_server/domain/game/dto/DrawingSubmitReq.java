package socket_server.domain.game.dto;

/**
 * 그림 제출 시 클라이언트에서 보내줄 DTO
 */
public record DrawingSubmitReq(
        String roundIndex,
        String imageData // Base64 문자열
) {
}
