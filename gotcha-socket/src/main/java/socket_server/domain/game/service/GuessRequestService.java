package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import socket_server.domain.game.repository.RoundRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuessRequestService {

    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final AIClientService aIClientService;
    private final GameBroadCaster gameBroadCaster;
    private final RoundRepository roundRepository;

    public Guess requestGuessAI(String roomId, GameMeta gameMeta,  Word guessTargetWord) {
        // 0. 현재 guess 개수 가져오기
        List<Guess> guesses = roundRepository.findAIGuesses(roomId, gameMeta.getCurrentRound(), guessTargetWord.getWordIndex());

        // 1. AI 서버에 Guess Request 메시지 받아옴
        String drawerUuid = guessTargetWord.getDrawerUuid();
        GamePlayer gamePlayer = gamePlayerRepository.findPlayerByUuid(roomId, drawerUuid);
        String drawerName = gamePlayer.getNickname();
        String aiSays = aIClientService.getGuessStartMessage(roomId, new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), drawerName, "AI"));

        // 2. BUILD NEW GUESS DATA
        Guess guess = Guess.builder().guesserUuid("AI").attempts(guesses.size()+1).build();

        // 3. 해당 정보 브로드캐스트
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, guess, aiSays, null);
        return guess;

    }

    /**
     *   "data": {
     *     "guess": {
     * 	    "guesserUuid": "playerB",
     * 	    "attempts" : 1,
     * 	    "guessWord": null, // 아직 추측하지 않음
     * 	    "correct": null // 아직 정답여부 나오지 않음
     *     },
     *     "aiSays": "마 함 맞춰봐라",
     *     "endTime" : 1621234567890
     *   }
     */
    public void requestGuessPlayer(String roomId, GameMeta gameMeta,  Word word) {
        // 1. Drawer, Guesser 가져옴
        List<GamePlayer> players = gamePlayerRepository.findPlayersByRoomId(roomId);
        GamePlayer gusser, drawer;
        if(word.getDrawerUuid().equals(players.get(0).getPlayerUuid())) {
            gusser = players.get(1);
            drawer = players.get(0);
        } else {
            gusser = players.get(0);
            drawer = players.get(1);
        }

        // 2. 현재 guess 개수 가져오기
        List<Guess> guesses = roundRepository.findPlayerGuesses(roomId, gameMeta.getCurrentRound(), word.getWordIndex());


        //3. AI 서버에 Guess Request 메시지 받아옴
        String aiSays = aIClientService.getGuessStartMessage(roomId,
                new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), drawer.getNickname(), gusser.getNickname()));

        // 3. BUILD NEW GUESS DATA
        Guess guess = Guess.builder().guesserUuid(gusser.getPlayerUuid()).attempts(guesses.size()+1).build();

        // 4. BroadCast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, guess, aiSays, null);
    }


}
