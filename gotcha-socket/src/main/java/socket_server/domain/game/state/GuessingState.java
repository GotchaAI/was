package socket_server.domain.game.state;

import reactor.core.publisher.Mono;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.*;
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
import java.util.Map;

/**
 * 게임의 추측 단계(Guessing Phase)를 나타내는 클래스.
 * 이 상태에서는 추측 시작(GUESS_START), 추측 요청(GUESS_REQUEST), 추측 제출(GUESS_SUBMIT) 이벤트를 처리합니다.
 */
public class GuessingState extends AbstractGameState {

    /**
     * 이벤트를 처리합니다.
     * GUESS_START, GUESS_REQUEST, GUESS_SUBMIT 이벤트를 처리하며, 다른 이벤트는 지원하지 않습니다.
     * @param context 게임 컨텍스트
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 인자 (GUESS_SUBMIT의 경우 guesserUuid, guessWord)
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    @Override
    public Mono<Void> handleEvent(GameContext context, GameEventType eventType, String... args) {
        return switch (eventType) {
            case GUESS_START -> handleGuessStart(context);
            case GUESS_REQUEST -> handleGuessRequest(context);
            case GUESS_SUBMIT -> handleGuessSubmit(context, args[0], args[1]);
            default -> GameState.super.handleEvent(context, eventType, args);
        };
    }

    /**
     * 이 상태의 GameStatus를 반환합니다.
     * @return GameStatus.GUESSING_STARTED
     */
    @Override
    public GameStatus getStatus() {
        return GameStatus.GUESSING_STARTED;
    }

    /**
     * 추측 단계 시작 이벤트를 처리합니다.
     * @param context 게임 컨텍스트
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleGuessStart(GameContext context) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

        Round currentRound = getCurrentRound(context.getRoomId(), manager);
        List<WordMeta> wordMetas = getWordMetas(context.getRoomId(), currentRound.getRoundIndex(), manager);
        WordMeta currentWord = wordMetas.get(currentRound.getCurrentWordIndex());

        manager.getGameBroadCaster().broadcastGameEvent("SYSTEM", context.getRoomId(), GameEventType.GUESS_START, currentWord, null);
        gameMeta.setGameStatus(GameStatus.GUESSING_STARTED);
        manager.getGameRepository().saveGameMeta(gameMeta);

        return Mono.delay(Duration.ofSeconds(1)).then(context.handleEvent(GameEventType.GUESS_REQUEST));
    }

    /**
     * 다음 추측 요청을 처리합니다. AI 또는 플레이어의 턴을 결정하고 해당 요청을 보냅니다.
     * @param context 게임 컨텍스트
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleGuessRequest(GameContext context) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

        Round currentRound = getCurrentRound(context.getRoomId(), manager);
        Word currentWord = getCurrentWord(context.getRoomId(), currentRound, manager);
        gameMeta.setGameStatus(GameStatus.GUESSING_REQUESTED);
        manager.getGameRepository().saveGameMeta(gameMeta);

        boolean isAITurn = determineNextGuesser(currentWord);
        if (isAITurn) {
            // AI 턴: AI 추측 요청 후 일정 시간 뒤 AI 추측 제출 처리
            return manager.getGuessRequestService().requestGuessAI(context.getRoomId(), gameMeta, currentWord)
                .flatMap(newGuess -> Mono.delay(Duration.ofSeconds(3))
                .then(handleAIGuessSubmit(context, currentRound, currentWord, newGuess)));
        } else {
            // 플레이어 턴: 플레이어 추측 요청
            return manager.getGuessRequestService().requestGuessPlayer(context.getRoomId(), gameMeta, currentWord);
        }
    }

    /**
     * 추측 제출 이벤트를 처리합니다. (플레이어 또는 AI)
     * @param context 게임 컨텍스트
     * @param guesserUuid 추측한 유저의 UUID
     * @param guessWord 추측 단어
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleGuessSubmit(GameContext context, String guesserUuid, String guessWord) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

        Round currentRound = getCurrentRound(context.getRoomId(), manager);
        Word currentWord = getCurrentWord(context.getRoomId(), currentRound, manager);

        // 유효성 검사: 그림을 그린 유저가 추측할 수 없음
        if (currentWord.getDrawerUuid().equals(guesserUuid)) {
            return Mono.error(new SocketCustomException(ErrorType.GAME, GameExceptionCode.INVALID_GUESSER));
        }

        // 유효성 검사: 이미 모든 추측 시도가 끝났거나 AI 턴인데 플레이어가 추측하는 경우
        if (determineNextGuesser(currentWord) || (currentWord.getPlayerGuesses().size() >= 3 && currentWord.getAiGuesses().size() >= 3)) {
            return Mono.error(new SocketCustomException(ErrorType.GAME, GameExceptionCode.INVALID_GUESSER));
        } else {
            // 추측 데이터 빌드
            Guess guess = Guess.builder()
                    .guesserUuid(guesserUuid)
                    .guessWord(guessWord)
                    .attempts(currentWord.getPlayerGuesses().size() + 1)
                    .build();

            // 추측 정보 브로드캐스트
            manager.getGameBroadCaster().broadcastGameEvent(guesserUuid, context.getRoomId(), GameEventType.GUESS_SUBMIT, guess, null);

            // 플레이어 추측 목록 업데이트 및 저장
            List<Guess> playerGuesses = getPlayerGuesses(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), manager);
            currentWord.setPlayerGuesses(playerGuesses);
            currentWord.getPlayerGuesses().add(guess);

            // 정답 여부 확인
            guess.setCorrect(guess.getGuessWord().equalsIgnoreCase(currentWord.getWord()));

            String playerGuessesJson = manager.getJsonSerializer().serialize(playerGuesses, ErrorType.GAME);
            manager.getRoundRepository().savePlayerGuesses(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), playerGuessesJson);

            gameMeta.setGameStatus(GameStatus.GUESSING_PROCESSING);
            manager.getGameRepository().saveGameMeta(gameMeta);

            // 일정 시간 뒤 추측 결과 처리
            return Mono.delay(Duration.ofSeconds(1)).then(handleGuessResult(context, currentRound, currentWord, guess));
        }
    }

    /**
     * AI의 추측 제출을 처리합니다.
     * @param context 게임 컨텍스트
     * @param currentRound 현재 라운드 정보
     * @param currentWord 현재 단어 정보
     * @param guess AI의 추측 정보
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleAIGuessSubmit(GameContext context, Round currentRound, Word currentWord, Guess guess) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

        List<AIPrediction> predictions = getAIPredictions(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), manager);
        String aiPredicted = predictions.get(guess.getAttempts() - 1).getPredicted();

        guess.setGuessWord(aiPredicted);

        // AI 서버에 추측 메시지 요청 및 응답 처리
        return manager.getAiClientService().getGuessMessage(context.getRoomId(), new AIGuessMessageReq(aiPredicted))
                .flatMap(aiSays -> {
                    manager.getGameBroadCaster().broadcastGameEvent("SYSTEM", context.getRoomId(), GameEventType.GUESS_SUBMIT, guess, aiSays);
                    gameMeta.setGameStatus(GameStatus.GUESSING_PROCESSING);
                    manager.getGameRepository().saveGameMeta(gameMeta);

                    guess.setCorrect(aiPredicted.equalsIgnoreCase(currentWord.getWord()));

                    List<Guess> aiGuesses = getAIGuesses(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), manager);
                    aiGuesses.add(guess);
                    currentWord.setAiGuesses(aiGuesses);

                    String aiGuessesString = manager.getJsonSerializer().serialize(aiGuesses, ErrorType.GAME);
                    manager.getRoundRepository().saveAIGuessesString(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), aiGuessesString);

                    return Mono.delay(Duration.ofSeconds(3)).then(handleGuessResult(context, currentRound, currentWord, guess));
                });
    }

    /**
     * 추측 결과(정답 여부)를 처리합니다.
     * @param context 게임 컨텍스트
     * @param currentRound 현재 라운드 정보
     * @param currentWord 현재 단어 정보
     * @param guess 처리할 추측 정보
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleGuessResult(GameContext context, Round currentRound, Word currentWord, Guess guess) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

        String guesserUuid = guess.getGuesserUuid();

        List<GamePlayer> gamePlayers = getGamePlayersByRoomId(context.getRoomId(), manager);
        GamePlayer guesser = gamePlayers.stream().filter(player -> player.getPlayerUuid().equals(guesserUuid)).findFirst().orElse(null);

        String guesserNickname = guesserUuid.equals("AI") ? "묘묘" : guesser.getNickname();

        // AI 서버에 추측 반응 메시지 요청 및 응답 처리
        return manager.getAiClientService().getGuessReactMessage(context.getRoomId(), new AIGuessReactReq(guess.getCorrect(), currentWord.getWord(), guesserNickname))
                .flatMap(aiSays -> {
                    manager.getGameBroadCaster().broadcastGameEvent("SYSTEM", context.getRoomId(), GameEventType.GUESS_RESULT, guess, aiSays);

                    // 단어 추측 완료 여부에 따라 다음 단계 결정
                    if (isWordGuessCompleted(currentWord)) {
                        return Mono.delay(Duration.ofSeconds(1)).then(handleBattleEnd(context, currentRound, currentWord));
                    } else {
                        return handleGuessRequest(context); // 다음 추측 요청
                    }
                });
    }

    /**
     * 단어 추측 배틀 종료를 처리하고 점수를 업데이트합니다.
     * @param context 게임 컨텍스트
     * @param currentRound 현재 라운드 정보
     * @param currentWord 현재 단어 정보
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleBattleEnd(GameContext context, Round currentRound, Word currentWord) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);
        gameMeta.setGameStatus(GameStatus.GUESSING_ENDED);
        manager.getGameRepository().saveGameMeta(gameMeta);

        List<Guess> playerGuesses = getPlayerGuesses(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), manager);
        List<Guess> aiGuesses = getAIGuesses(context.getRoomId(), currentRound.getRoundIndex(), currentWord.getWordIndex(), manager);

        boolean isPlayerWin = false;
        int score = 0;
        for (Guess playerGuess : playerGuesses) {
            if (playerGuess.getCorrect()) {
                score = 10 * (3 - playerGuess.getAttempts() + 1);
                isPlayerWin = true;
                break;
            }
        }
        for (Guess aiGuess : aiGuesses) {
            if (aiGuess.getCorrect()) {
                score = 10 * (3 - aiGuess.getAttempts() + 1);
                break;
            }
        }

        List<WordMeta> wordMetas = getWordMetas(context.getRoomId(), currentRound.getRoundIndex(), manager);
        wordMetas.get(currentWord.getWordIndex()).setPlayerWon(isPlayerWin);
        wordMetas.get(currentWord.getWordIndex()).setScore(score);

        saveWordMetas(context.getRoomId(), currentRound.getRoundIndex(), wordMetas, manager);

        List<Boolean> allPlayerWons = new ArrayList<>();
        for (int i = 0; i < gameMeta.getTotalRounds(); i++) {
            List<WordMeta> roundWordMetas = getWordMetas(context.getRoomId(), i, manager);
            for (int j = 0; j < 2; j++) {
                allPlayerWons.add(roundWordMetas.get(j).getPlayerWon());
            }
        }

        manager.getGameBroadCaster().broadcastGameEvent("SYSTEM", context.getRoomId(), GameEventType.BATTLE_END, allPlayerWons, null);
        currentRound.setCurrentWordIndex(currentRound.getCurrentWordIndex() + 1);

        List<RoundMeta> roundMetas = getRoundMetas(context.getRoomId(), manager);
        roundMetas.get(currentRound.getRoundIndex()).setCurrentWordIndex(currentRound.getCurrentWordIndex());
        saveRoundMetas(context.getRoomId(), roundMetas, manager);

        // 다음 단어 또는 라운드/게임 종료 결정
        if (currentRound.getCurrentWordIndex() >= 2) { // 현재 라운드의 모든 단어 추측 완료
            context.setState(new RoundEndState());
            return Mono.delay(Duration.ofSeconds(1)).then(context.handleEvent(GameEventType.ROUND_END));
        } else { // 다음 단어 추측 시작
            context.setState(new DrawingPhaseState()); // TODO: Should be GuessingState for next word in same round
            return Mono.delay(Duration.ofSeconds(3)).then(context.handleEvent(GameEventType.ROUND_START)); // TODO: Should be GUESS_START for next word
        }
    }

    /**
     * 현재 단어 정보를 가져옵니다.
     * @param roomId 게임방 ID
     * @param round 현재 라운드 정보
     * @param manager GameFlowManager 인스턴스
     * @return 현재 단어 객체
     */
    private Word getCurrentWord(String roomId, Round round, GameFlowManager manager) {
        List<Guess> playerGuesses = getPlayerGuesses(roomId, round.getRoundIndex(), round.getCurrentWordIndex(), manager);
        List<Guess> aiGuesses = getAIGuesses(roomId, round.getRoundIndex(), round.getCurrentWordIndex(), manager);

        Word word = round.getWords().get(round.getCurrentWordIndex());
        word.setPlayerGuesses(playerGuesses);
        word.setAiGuesses(aiGuesses);

        return word;
    }

    /**
     * 현재 라운드 정보를 가져옵니다.
     * @param roomId 게임방 ID
     * @param manager GameFlowManager 인스턴스
     * @return 현재 라운드 객체
     */
    private Round getCurrentRound(String roomId, GameFlowManager manager) {
        GameMeta gameMeta = getGameMetaByRoomId(roomId, manager);
        int roundIndex = gameMeta.getCurrentRound();
        String roundsJson = manager.getRoundRepository().findRoundMetasString(roomId);
        List<RoundMeta> roundMetas = manager.getJsonSerializer().deserializeList(roundsJson, RoundMeta.class, ErrorType.GAME);
        RoundMeta roundMeta = roundMetas.get(roundIndex);

        List<WordMeta> wordMetas = getWordMetas(roomId, roundMeta.getRoundIndex(), manager);
        List<Word> words = wordMetas.stream().map(WordMeta::toWord).toList();

        Round round = RoundMeta.toRound(roundMeta);
        round.setWords(words);
        return round;
    }

    /**
     * 라운드의 단어 메타데이터를 조회합니다.
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
     * 다음 추측자가 AI인지 플레이어인지 결정합니다.
     * @param word 현재 단어 정보
     * @return AI 턴이면 true, 플레이어 턴이면 false
     */
    private boolean determineNextGuesser(Word word) {
        return word.getAiGuesses().size() == word.getPlayerGuesses().size();
    }

    /**
     * 단어 추측이 완료되었는지 확인합니다.
     * @param word 현재 단어 정보
     * @return 단어 추측이 완료되었으면 true, 아니면 false
     */
    private boolean isWordGuessCompleted(Word word) {
        boolean hasCorrectGuess = word.getAiGuesses().stream().anyMatch(Guess::getCorrect) ||
                word.getPlayerGuesses().stream().anyMatch(Guess::getCorrect);
        if (hasCorrectGuess) return true;

        int aiAttempts = word.getAiGuesses().size();
        int playerAttempts = word.getPlayerGuesses().size();

        return aiAttempts >= 3 && playerAttempts >= 3;
    }

    /**
     * 단어 메타데이터를 저장합니다.
     * @param roomId 게임방 ID
     * @param roundIndex 라운드 인덱스
     * @param wordMetas 저장할 단어 메타데이터 목록
     * @param manager GameFlowManager 인스턴스
     */
    private void saveWordMetas(String roomId, int roundIndex, List<WordMeta> wordMetas, GameFlowManager manager) {
        String wordMetasJson = manager.getJsonSerializer().serialize(wordMetas, ErrorType.GAME);
        manager.getRoundRepository().saveWordMetasString(roomId, roundIndex, wordMetasJson);
    }

    /**
     * 라운드 메타데이터를 저장합니다.
     * @param roomId 게임방 ID
     * @param roundMetas 저장할 라운드 메타데이터 목록
     * @param manager GameFlowManager 인스턴스
     */
    private void saveRoundMetas(String roomId, List<RoundMeta> roundMetas, GameFlowManager manager) {
        manager.getRoundRepository().saveRoundMetasString(roomId, manager.getJsonSerializer().serialize(roundMetas, ErrorType.GAME));
    }
}
