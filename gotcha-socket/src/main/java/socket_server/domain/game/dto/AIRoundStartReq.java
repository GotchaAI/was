package socket_server.domain.game.dto;

/**
 * 라운드 시작 시 AI 서버에 보낼 DTO
 */
public record AIRoundStartReq(
        int roundNum,
        int totalRounds
) {
}
