package socket_server.domain.game.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.room.service.RoomService;

@RequiredArgsConstructor
@Service
public class GameService {
    /**
     * 외부 요청 처리용 GameService
     */

    private final GameFlowService gameFlowService;
    private final RoundService roundService;


    public void startGame(String roomId, String userUuid) {
        gameFlowService.startGame(roomId, userUuid);
    }

    public void submitDrawing(String roomId, String userUuid, String drawing) {
        roundService.submitDrawing(roomId, userUuid, drawing);
    }

}
