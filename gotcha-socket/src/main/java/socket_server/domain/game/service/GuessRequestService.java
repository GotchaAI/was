package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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

/**
 * 게임 내 추측 요청을 처리하는 서비스 클래스.
 * AI 및 플레이어의 추측 요청을 비동기적으로 처리하도록 리팩토링되었습니다.
 */
@Service
@RequiredArgsConstructor
public class GuessRequestService {
    private final GamePlayerRepository gamePlayerRepository;
    private final AIClientService aIClientService;
    private final GameBroadCaster gameBroadCaster;
    private final RoundRepository roundRepository;
    private final JsonSerializer jsonSerializer;
    private final ErrorType GAME_ERROR = ErrorType.GAME;

    /**
     * AI의 추측 요청을 처리하고, AI 서버로부터 메시지를 받아옵니다.
     * @param roomId 게임방 ID
     * @param gameMeta 현재 게임의 메타데이터
     * @param guessTargetWord 추측 대상 단어
     * @return AI의 추측 정보를 담은 Mono<Guess> 객체
     */
    public Mono<Guess> requestGuessAI(String roomId, GameMeta gameMeta,  Word guessTargetWord) {
        List<Guess> guesses = getAIGuesses(roomId, gameMeta.getCurrentRound(), guessTargetWord.getWordIndex());

        return aIClientService.getGuessStartMessage(roomId, new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), guessTargetWord.getDrawerName(), "AI"))
            .map(aiSays -> {
                Guess guess = Guess.builder().guesserUuid("AI").attempts(guesses.size()+1).guessEndTime(LocalDateTime.now().plusSeconds(31)).build();
                gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, guess, aiSays);
                return guess;
            });
    }

    /**
     * 플레이어의 추측 요청을 처리하고, AI 서버로부터 메시지를 받아옵니다.
     * @param roomId 게임방 ID
     * @param gameMeta 현재 게임의 메타데이터
     * @param word 추측 대상 단어
     * @return 비동기 완료를 나타내는 Mono<Void> 객체
     */
    public Mono<Void> requestGuessPlayer(String roomId, GameMeta gameMeta,  Word word) {
        List<GamePlayer> players = getGamePlayersByRoomId(roomId);
        GamePlayer gusser, drawer;
        if(word.getDrawerUuid().equals(players.get(0).getPlayerUuid())) {
            gusser = players.get(1);
            drawer = players.get(0);
        } else {
            gusser = players.get(0);
            drawer = players.get(1);
        }

        List<Guess> guesses = getPlayerGuesses(roomId, gameMeta.getCurrentRound(), word.getWordIndex());

        return aIClientService.getGuessStartMessage(roomId,
                new AIGuessStartReq(gameMeta.getCurrentRound(), gameMeta.getTotalRounds(), drawer.getNickname(), gusser.getNickname()))
            .doOnSuccess(aiSays -> {
                Guess guess = Guess.builder().guesserUuid(gusser.getPlayerUuid()).attempts(guesses.size()+1).build();
                guess.setGuessEndTime(LocalDateTime.now().plusSeconds(32));
                gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_REQUEST, guess, aiSays);
            }).then();
    }

    private List<GamePlayer> getGamePlayersByRoomId(String roomId) {
        String playersJson = gamePlayerRepository.findPlayersStringByRoomId(roomId);
        return jsonSerializer.deserializeList(playersJson, GamePlayer.class, GAME_ERROR);
    }

    private List<Guess> getAIGuesses(String roomId, int roundIndex, int wordIndex){
        String aiGuessString = roundRepository.findAIGuessesString(roomId, roundIndex, wordIndex);
        if(aiGuessString == null) return List.of();
        return jsonSerializer.deserializeList(aiGuessString, Guess.class, GAME_ERROR);
    }

    private List<Guess> getPlayerGuesses(String roomId, int roundIndex, int wordIndex){
        String playerGuessString = roundRepository.findPlayerGuessesString(roomId, roundIndex, wordIndex);
        if(playerGuessString == null) return List.of();
        return jsonSerializer.deserializeList(playerGuessString, Guess.class, GAME_ERROR);
    }

}
