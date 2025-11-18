package socket_server.domain.game.dto;

/**
 *  추측 시작 시(GUESS_REQUEST) AI서버로 보낼 DTO
 */
public record AIGuessStartReq(
        int round_num,
        int total_rounds,
        String drawer,
        String guesser
) {
}
