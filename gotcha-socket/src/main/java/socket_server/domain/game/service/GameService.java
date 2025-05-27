package socket_server.domain.game.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void submitDrawing(String roomId, String userUuid, String imageURL) {
        roundService.submitDrawing(roomId, userUuid, imageURL);
        if(roundService.checkAllDrawingSubmitted(roomId)){
            startGuessing(roomId);
        }
    }


    public void startGuessing(String roomId){
        gameFlowService.startGuessing(roomId);
    }

}
