package socket_server.domain.game.dto;


public record AIRoundEndReq(
        int roundNum,
        int totalRounds,
        String winner
) {

}
