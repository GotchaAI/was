package socket_server.domain.game.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameType {
    TRICK_MYOMYO("묘묘를 속여라", 2, 2),
    LULU_ART_EXAM("루루의 미대입시", 1, 1);

    private final String description;
    private final int minPlayers;
    private final int maxPlayers;


}
