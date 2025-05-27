package socket_server.domain.game.dto;

public record AIRoundStartReq(
        int roundNum,
        int totalRounds
) {
}
