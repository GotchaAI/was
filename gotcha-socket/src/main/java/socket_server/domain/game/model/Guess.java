package socket_server.domain.game.model;

import gotcha_domain.gamehistory.GuessInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게임 속 제시어에 대한 추측 데이터(Redis에 저장)
 */
@Builder
@Data
public class Guess {
    private String guesserUuid;
    private String guessWord;
    private int attempts;
    private Boolean correct;
    private LocalDateTime guessEndTime;

    public static GuessInfo fromGuess(Guess guess) {
        return GuessInfo.builder()
                .guesserUuid(guess.getGuesserUuid())
                .guessWord(guess.getGuessWord())
                .attempts(guess.getAttempts())
                .correct(guess.getCorrect())
                .build();
    }
}
