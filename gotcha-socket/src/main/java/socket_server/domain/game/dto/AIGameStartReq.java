package socket_server.domain.game.dto;

import java.util.List;

public record AIGameStartReq(
        List<String> players
) {
}
