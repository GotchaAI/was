package socket_server.domain.game.state;

import reactor.core.publisher.Mono;
import socket_server.common.exception.ErrorType;
import socket_server.domain.game.dto.AIGameEndReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.*;
import socket_server.domain.game.service.GameFlowManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 게임의 최종 종료 상태를 나타내는 클래스.
 * 이 상태에서는 게임 종료(GAME_END) 이벤트를 처리합니다.
 */
public class GameEndState extends AbstractGameState {

    /**
     * 이벤트를 처리합니다.
     * GAME_END 이벤트가 발생하면 게임 결과를 최종 처리하고 점수를 업데이트합니다.
     * @param context 게임 컨텍스트
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 인자 (사용되지 않음)
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    @Override
    public Mono<Void> handleEvent(GameContext context, GameEventType eventType, String... args) {
        if (eventType == GameEventType.GAME_END) {
            GameFlowManager manager = context.getGameFlowManager();
            GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

            gameMeta.setGameStatus(GameStatus.GAME_ENDED);

            List<Round> rounds = getRounds(context.getRoomId(), manager);

            Game game = Game.fromGameMeta(gameMeta);
            game.setRounds(rounds);

            List<GamePlayer> gamePlayers = getGamePlayersByRoomId(context.getRoomId(), manager);
            game.setGamePlayers(gamePlayers);

            determineGameWinnerAndCalculateScores(game);

            manager.getGameRepository().saveGameMeta(GameMeta.fromGame(game));

            // AI 서버에 게임 종료 메시지 요청 및 응답 처리
            return manager.getAiClientService().getGameEndMessage(context.getRoomId(), new AIGameEndReq(game.getPlayerWon() ? "PLAYER" : "AI"))
                    .flatMap(aiSays -> {
                        manager.getGameBroadCaster().broadcastGameEvent("SYSTEM", context.getRoomId(), GameEventType.GAME_END, game, aiSays);
                        // 점수 업데이트 후 비동기 완료
                        return Mono.delay(Duration.ofSeconds(1)).then(manager.getGameEndService().updateScore(game));
                    });
        }
        return super.handleEvent(context, eventType, args);
    }

    /**
     * 이 상태의 GameStatus를 반환합니다.
     * @return GameStatus.GAME_ENDED
     */
    @Override
    public GameStatus getStatus() {
        return GameStatus.GAME_ENDED;
    }

    /**
     * 모든 라운드 데이터를 조회하고 단어, 추측, 예측 데이터를 연결합니다.
     * @param roomId 게임방 ID
     * @param manager GameFlowManager 인스턴스
     * @return 모든 라운드 데이터가 연결된 목록
     */
    private List<Round> getRounds(String roomId, GameFlowManager manager) {
        List<RoundMeta> roundMetas = getRoundMetas(roomId, manager);
        List<Round> rounds = roundMetas.stream().map(RoundMeta::toRound).toList();

        for (Round round : rounds) {
            List<Word> words = getWordMetas(roomId, round.getRoundIndex(), manager)
                    .stream().map(WordMeta::toWord).toList();

            for (Word word : words) {
                List<Guess> aiGuesses = getAIGuesses(roomId, round.getRoundIndex(), word.getWordIndex(), manager);
                List<Guess> playerGuesses = getPlayerGuesses(roomId, round.getRoundIndex(), word.getWordIndex(), manager);
                List<AIPrediction> AIPredictions = getAIPredictions(roomId, round.getRoundIndex(), word.getWordIndex(), manager);

                word.setAiGuesses(aiGuesses);
                word.setPlayerGuesses(playerGuesses);
                word.setAIPredictions(AIPredictions);
            }
            round.setWords(words);
        }
        return rounds;
    }

    /**
     * 라운드 메타데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param manager GameFlowManager 인스턴스
     * @return 라운드 메타데이터 목록
     */
    private List<RoundMeta> getRoundMetas(String roomId, GameFlowManager manager) {
        String roundsJson = manager.getRoundRepository().findRoundMetasString(roomId);
        return manager.getJsonSerializer().deserializeList(roundsJson, RoundMeta.class, ErrorType.GAME);
    }

    /**
     * 단어 메타데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param roundIndex 라운드 인덱스
     * @param manager GameFlowManager 인스턴스
     * @return 단어 메타데이터 목록
     */
    private List<WordMeta> getWordMetas(String roomId, int roundIndex, GameFlowManager manager) {
        String wordMetasJson = manager.getRoundRepository().findWordMetasString(roomId, roundIndex);
        return manager.getJsonSerializer().deserializeList(wordMetasJson, WordMeta.class, ErrorType.GAME);
    }

    /**
     * AI 예측 데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param roundIndex 라운드 인덱스
     * @param wordIndex 단어 인덱스
     * @param manager GameFlowManager 인스턴스
     * @return AI 예측 데이터 목록
     */
    private List<AIPrediction> getAIPredictions(String roomId, int roundIndex, int wordIndex, GameFlowManager manager) {
        String aiPredictionString = manager.getRoundRepository().findAIPredictionsString(roomId, roundIndex, wordIndex);
        return manager.getJsonSerializer().deserializeList(aiPredictionString, AIPrediction.class, ErrorType.GAME);
    }

    /**
     * AI 추측 데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param roundIndex 라운드 인덱스
     * @param wordIndex 단어 인덱스
     * @param manager GameFlowManager 인스턴스
     * @return AI 추측 데이터 목록
     */
    private List<Guess> getAIGuesses(String roomId, int roundIndex, int wordIndex, GameFlowManager manager) {
        String aiGuessString = manager.getRoundRepository().findAIGuessesString(roomId, roundIndex, wordIndex);
        if (aiGuessString == null) return new ArrayList<>();
        return manager.getJsonSerializer().deserializeList(aiGuessString, Guess.class, ErrorType.GAME);
    }

    /**
     * 플레이어 추측 데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param roundIndex 라운드 인덱스
     * @param wordIndex 단어 인덱스
     * @param manager GameFlowManager 인스턴스
     * @return 플레이어 추측 데이터 목록
     */
    private List<Guess> getPlayerGuesses(String roomId, int roundIndex, int wordIndex, GameFlowManager manager) {
        String playerGuessString = manager.getRoundRepository().findPlayerGuessesString(roomId, roundIndex, wordIndex);
        if (playerGuessString == null) return new ArrayList<>();
        return manager.getJsonSerializer().deserializeList(playerGuessString, Guess.class, ErrorType.GAME);
    }

    /**
     * 게임 플레이어 목록을 조회합니다.
     * @param roomId 게임방 ID
     * @param manager GameFlowManager 인스턴스
     * @return 게임 플레이어 목록
     */
    private List<GamePlayer> getGamePlayersByRoomId(String roomId, GameFlowManager manager) {
        String playersJson = manager.getGamePlayerRepository().findPlayersStringByRoomId(roomId);
        return manager.getJsonSerializer().deserializeList(playersJson, GamePlayer.class, ErrorType.GAME);
    }

    /**
     * 게임 승자를 결정하고 점수를 계산합니다.
     * @param game 게임 객체
     */
    private void determineGameWinnerAndCalculateScores(Game game) {
        List<Word> words = new ArrayList<>();
        for (Round round : game.getRounds()) {
            words.addAll(round.getWords());
        }

        int playerScore = 0;
        int aiScore = 0;
        for (Word word : words) {
            if (word.getPlayerWon()) {
                playerScore += word.getScore();
            } else {
                aiScore += word.getScore();
            }
        }

        game.setPlayerScore(playerScore);
        game.setAiScore(aiScore);
        game.setPlayerWon(playerScore > aiScore);
    }
}
