package socket_server.domain.game.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
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

    public void startGame(String roomId, String userUuid, ErrorType errorType) {
        gameFlowService.startGame(roomId, userUuid, errorType);
    }


}
