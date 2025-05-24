package socket_server.domain.game.model;

import lombok.Builder;
import lombok.Data;

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
}
