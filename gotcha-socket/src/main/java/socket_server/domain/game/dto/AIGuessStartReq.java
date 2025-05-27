package socket_server.domain.game.dto;

/**
 *  AI 추측 시작 시 AI서버로 보낼 DTO
 */
public record AIGuessStartReq(
        int roundNum,
        int totalRounds,
        String drawer,
        String guesser
) {
}
