package socket_server.domain.game.dto;

import java.util.List;

public record AIGameStartRequest(
        List<String> players
) {
}
