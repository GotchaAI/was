package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.AIGuessStartReq;
import socket_server.domain.game.dto.AISaysRes;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;

@Service
@RequiredArgsConstructor
public class GuessRequestService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final AIClientService aIClientService;
    private final GameBroadCaster gameBroadCaster;
    private final GuessSubmitService guessSubmitService;

    public void requestGuessAI(String roomId, Round currentRound, Word guessTargetWord) {
        // 1. AI 서버에 Guess Request 메시지 받아옴
        String drawerUuid = guessTargetWord.getDrawerUuid();
        GamePlayer gamePlayer = gamePlayerRepository.findPlayerByUuid(roomId, drawerUuid);
        String drawerName = gamePlayer.getNickname();
        String aiSays = aIClientService.getGuessStartMessage(roomId, new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), drawerName));

        // 2. 해당 정보 브로드캐스트
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, new AISaysRes(guess, aiSays));



    }

    public void requestGuessPlayer(String roomId, Round currentRound, Word guessTargetWord) {

    }


}
