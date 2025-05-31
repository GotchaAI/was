package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.AIGuessStartReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuessRequestService {
    private final GamePlayerRepository gamePlayerRepository;
    private final AIClientService aIClientService;
    private final GameBroadCaster gameBroadCaster;
    private final RoundRepository roundRepository;
    private final JsonSerializer jsonSerializer;
    private final ErrorType GAME_ERROR = ErrorType.GAME;

    public Guess requestGuessAI(String roomId, GameMeta gameMeta,  Word guessTargetWord) {
        // 0. 현재 guess 개수 가져오기
        List<Guess> guesses = getAIGuesses(roomId, gameMeta.getCurrentRound(), guessTargetWord.getWordIndex());

        // 1. AI 서버에 Guess Request 메시지 받아옴
        String drawerUuid = guessTargetWord.getDrawerUuid();
        String playerJson = gamePlayerRepository.findGamePlayerStringByUuid(roomId, drawerUuid);
        GamePlayer gamePlayer = jsonSerializer.deserialize(playerJson, GamePlayer.class, GAME_ERROR);
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
        List<GamePlayer> players = getGamePlayersByRoomId(roomId);
        GamePlayer gusser, drawer;
        if(word.getDrawerUuid().equals(players.get(0).getPlayerUuid())) {
            gusser = players.get(1);
            drawer = players.get(0);
        } else {
            gusser = players.get(0);
            drawer = players.get(1);
        }

        // 2. 현재 guess 개수 가져오기
        List<Guess> guesses = getPlayerGuesses(roomId, gameMeta.getCurrentRound(), word.getWordIndex());


        //3. AI 서버에 Guess Request 메시지 받아옴
        String aiSays = aIClientService.getGuessStartMessage(roomId,
                new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), drawer.getNickname(), gusser.getNickname()));

        // 3. BUILD NEW GUESS DATA
        Guess guess = Guess.builder().guesserUuid(gusser.getPlayerUuid()).attempts(guesses.size()+1).build();

        // 4. BroadCast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, guess, aiSays, LocalDateTime.now().plusSeconds(30));
    }

    private List<GamePlayer> getGamePlayersByRoomId(String roomId) {
        List<String> playerUuids = gamePlayerRepository.findPlayerUuidsByRoomId(roomId);
        return playerUuids.stream()
                .map(uuid -> gamePlayerRepository.findGamePlayerStringByUuid(roomId, uuid))
                .map(gamePlayerJson -> jsonSerializer.deserialize(gamePlayerJson, GamePlayer.class, GAME_ERROR))
                .collect(Collectors.toList());
    }

    private List<Guess> getAIGuesses(String roomId, int roundIndex, int wordIndex){
        String aiGuessString = roundRepository.findAIGuessesString(roomId, roundIndex, wordIndex);
        return jsonSerializer.deserializeList(aiGuessString, Guess.class, GAME_ERROR);
    }

    private List<Guess> getPlayerGuesses(String roomId, int roundIndex, int wordIndex){
        String playerGuessString = roundRepository.findPlayerGuessesString(roomId, roundIndex, wordIndex);
        return jsonSerializer.deserializeList(playerGuessString, Guess.class, GAME_ERROR);
    }

}
