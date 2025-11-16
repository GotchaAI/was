package socket_server.domain.game.dto;

public record AIGuessReactReq(
        boolean is_correct,
        String answer,
        String guesser
) {
}
