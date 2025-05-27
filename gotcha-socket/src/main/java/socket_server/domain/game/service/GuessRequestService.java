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
        // 0. 게임 메타정보 조회 -> GameStatus 확인
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_REQUEST)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        // 1. AI 서버에 Guess Request 메시지 받아옴
        String drawerUuid = guessTargetWord.getDrawerUuid();
        GamePlayer gamePlayer = gamePlayerRepository.findPlayerByUuid(roomId, drawerUuid);
        String drawerName = gamePlayer.getNickname();
        String aiSays = aIClientService.getGuessStartMessage(roomId, new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), drawerName));

        // 2. BUILD NEW GUESS DATA
        Guess guess = Guess.builder().guesserUuid("AI").attempts(1).build();

        // 3. 해당 정보 브로드캐스트
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, new AISaysRes(guess, aiSays));

        // todo: 4. 실제 AI 추측 시작
        guessSubmitService.submitGuessAI(roomId, currentRound, guessTargetWord, guess);


    }


}
