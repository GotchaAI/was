package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.AIRoundStartReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoundStartService {
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final AIClientService aIClientService;
    private final ErrorType GAME_ERROR = ErrorType.GAME;
    private final JsonSerializer jsonSerializer;

    public void startNextRound(String roomId) {
        GameMeta gameMeta = getGameMetaByRoomId(roomId);

        if (!isGameEnded(gameMeta)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.ALREADY_FINISHED_GAME);
        }

        if (!gameMeta.getGameStatus().canHandleEvent(GameEventType.ROUND_START)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_STATUS);
        }

        int currentRound = gameMeta.getCurrentRound() + 1;

        String roundMetasJson =roundRepository.findRoundMetasString(roomId);
        List<RoundMeta> roundMetaList = jsonSerializer.deserializeList(roundMetasJson, RoundMeta.class, GAME_ERROR);

        gameMeta.setCurrentRound(currentRound);
        gameMeta.setGameStatus(GameStatus.DRAWING_PHASE); // ROUND_STARTED 생략 가능
        gameRepository.saveGameMeta(gameMeta);

        RoundMeta currentRoundMeta = roundMetaList.get(currentRound - 1);

        roundRepository.saveRoundMetasString(roomId, jsonSerializer.serialize(roundMetaList, GAME_ERROR));

        String aiSays = aIClientService.getRoundStartMessage(
                roomId,
                new AIRoundStartReq(currentRound, gameMeta.getTotalRounds())
        );
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            currentRoundMeta.setDrawingEndTime(LocalDateTime.now().plusSeconds(30));
            gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.ROUND_START, currentRoundMeta, aiSays);
        }, 5, TimeUnit.SECONDS);


    }

    private GameMeta getGameMetaByRoomId(String roomId) {
        Map<Object, Object> gameMetaMap = gameRepository.findGameMeta(roomId);
        if(gameMetaMap.isEmpty()) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_ID);
        }
        return GameMeta.fromRedisMap(roomId, gameMetaMap);
    }

    // 게임 종료 check시 반드시 필요
    public boolean isGameEnded(GameMeta gameMeta){
        return gameMeta.getCurrentRound() <= gameMeta.getTotalRounds();
    }
}
