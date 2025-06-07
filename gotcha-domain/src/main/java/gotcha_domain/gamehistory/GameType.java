package gotcha_domain.gamehistory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GameType {
    TRICK_MYOMYO("묘묘를 속여라", 2, 2),
    LULU_ART_EXAM("루루의 미대입시", 1, 1);

    @Getter
    private final String description;
    @Getter
    private final int minPlayers;

    @Getter
    private final int maxPlayers;



}
