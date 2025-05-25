package socket_server.domain.game.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.service.RoomService;

@RequiredArgsConstructor
@Service
public class GameService {
    /**
     * 도메인 데이터 Game 처리 담당.
     */

    private final RoomService roomService;
    private final GameRepository gameRepository;

    public RoomMetadata validateGameStart(String roomId, String userUuid){
        RoomMetadata roomMetadata = roomService.getHostingRoomMetadata(roomId, userUuid);
        roomService.checkGameStartable(roomId, roomMetadata.getGameType());
        return roomMetadata;
    }

    public Game initGame(String roomId, RoomMetadata roomMetadata) {
        return Game.builder().
                roomId(roomId).
                gameType(roomMetadata.getGameType()).
                difficulty(roomMetadata.getDifficulty()).
                currentRound(1).
                totalRounds(roomMetadata.getRoundCount()).build();
    }

    public void saveGameMeta(Game game){
        gameRepository.saveGameMeta(GameMeta.fromGame(game));
    }

}
