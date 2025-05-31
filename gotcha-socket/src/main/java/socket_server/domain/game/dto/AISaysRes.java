package socket_server.domain.game.dto;

/**
 * GPT API 메시지를 포함한 게임 데이터 반환용 DTO
 */
public record AISaysRes(
        Object gameData,
        String aiSays
) {
}
