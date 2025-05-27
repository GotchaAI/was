package socket_server.domain.game.dto;

public record AIGuessReactReq(
        boolean isCorrect,
        String answer,
        String guesser
) {
}
