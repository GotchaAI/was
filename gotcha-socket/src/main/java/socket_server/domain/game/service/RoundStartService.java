package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.AIRoundStartReq;
import socket_server.domain.game.dto.AISaysRes;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundStartService {
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final AIClientService aIClientService;
    private final ErrorType GAME_ERROR = ErrorType.GAME;

    public void startNextRound(String roomId) {
        GameMeta gameMeta = gameRepository.findGameMeta(roomId, GAME_ERROR);

        if (!isGameEnded(gameMeta)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.ALREADY_FINISHED_GAME);
        }

        if (!gameMeta.getGameStatus().canHandleEvent(GameEventType.ROUND_START)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_STATUS);
        }

        int currentRound = gameMeta.getCurrentRound() + 1;
        List<RoundMeta> roundMetaList = roundRepository.findRoundMetas(roomId, GAME_ERROR);

        gameMeta.setCurrentRound(currentRound);
        gameMeta.setGameStatus(GameStatus.DRAWING_PHASE); // ROUND_STARTED 생략 가능
        gameRepository.saveGameMeta(gameMeta);

        RoundMeta currentRoundMeta = roundMetaList.get(currentRound - 1);
        currentRoundMeta.setDrawingEndTime(LocalDateTime.now().plusSeconds(30));
        roundRepository.saveRoundMetas(roomId, roundMetaList, GAME_ERROR);

        String aiSays = aIClientService.getRoundStartMessage(
                roomId,
                new AIRoundStartReq(currentRound, gameMeta.getTotalRounds())
        );

        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.ROUND_START, currentRoundMeta, aiSays, null);
    }

    // 게임 종료 check시 반드시 필요
    public boolean isGameEnded(GameMeta gameMeta){
        return gameMeta.getCurrentRound() <= gameMeta.getTotalRounds();
    }
}
