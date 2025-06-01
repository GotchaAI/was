package gotcha_domain.gamehistory;

import lombok.*;

@Data
@Builder
public class GamePlayerInfo {
    private String playerUuid;
    private String nickname;
}