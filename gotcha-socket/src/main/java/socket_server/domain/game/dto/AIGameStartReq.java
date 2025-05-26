package socket_server.domain.game.dto;

import java.util.List;

/**
 * 게임 시작 시 AI 서버에 보낼 DTO
 */
public record AIGameStartReq(
        List<String> players
) {
}
