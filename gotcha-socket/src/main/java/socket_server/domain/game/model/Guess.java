package socket_server.domain.game.model;

/**
 * 게임 속 제시어에 대한 추측 데이터(Redis에 저장)
 */
public class Guess {
    private int roundIndex;
    private String guesserUuid;
    private String guessWord;
    private int attempts;
    private Boolean correct;
}
