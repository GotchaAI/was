package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.*;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.*;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * todo: 너무 많은 코드 !!! 리팩토링 필요
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuessFlowService {

    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final GuessRequestService guessRequestService;
    private final AIClientService aiClientService;
    private final GamePlayerRepository gamePlayerRepository;
    private final RoundStartService roundStartService;
    private final JsonSerializer jsonSerializer;

    private final ErrorType GAME_ERROR = ErrorType.GAME;

    /**
     * GUESS_START (ENTRY_POINT)
     * DRAWING_PHASE -> GUESSING_PHASE
     */
    public void startGuessingPhase(String roomId) {
        // 0. 게임 메타정보 조회 -> GameStatus 업데이트
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_START);


        gameMeta.setGameStatus(GameStatus.GUESSING_PHASE);
        gameRepository.saveGameMeta(gameMeta);

        // current wordmeta 조회
        Round currentRound = getCurrentRound(roomId);
        List<WordMeta> wordMetas = getWordMetas(roomId, currentRound.getRoundIndex());
        WordMeta currentWord = wordMetas.get(currentRound.getCurrentWordIndex());


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            gameBroadCaster.broadcastGameEvent("SYSTEM", roomId,GameEventType.GUESS_START, currentWord, null);
            processNextGuessRequest(roomId);
        }, 5, TimeUnit.SECONDS);
    }





    /**
     * GUESS_REQUEST 이벤트 발행.
     * 현재 GUESS 상태 확인.
     * 1. Round 종료 여부 확인
     * 2. Guess 완료 여부 확인
     * 3. check whether AI turn or Player turn
     */
    public void processNextGuessRequest(String roomId){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_REQUEST);

        // 1. 라운드 데이터 전체 조회
        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(roomId, currentRound);

        if(currentWord == null){
            handleRoundEnd(roomId);
            return;
        }

        if(isWordGuessCompleted(currentWord)){

            log.info("WordGuessCompleted : {}", currentWord);
            handleBattleEnd(roomId, currentRound, currentWord);
            moveToNextWord(roomId, currentRound);
            return;
        }



        boolean isAITurn = determineNextGuesser(currentWord);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            if(isAITurn){ // next guess
                Guess newGuess = guessRequestService.requestGuessAI(roomId, gameMeta, currentWord);
                handleAIGuessSubmit(roomId, currentRound, currentWord, newGuess);
            } else {
                guessRequestService.requestGuessPlayer(roomId, gameMeta, currentWord);
            }
        }, 5, TimeUnit.SECONDS);
    }

    private List<AiPrediction> getAIPredictions(String roomId, int roundIndex, int wordIndex){
        String aiPredictionString = roundRepository.findAIPredictionsString(roomId, roundIndex, wordIndex);
        return jsonSerializer.deserializeList(aiPredictionString, AiPrediction.class, GAME_ERROR);
    }



    private List<Guess> getAIGuesses(String roomId, int roundIndex, int wordIndex){
        String aiGuessString = roundRepository.findAIGuessesString(roomId, roundIndex, wordIndex);
        if(aiGuessString == null) return new ArrayList<>();
        return jsonSerializer.deserializeList(aiGuessString, Guess.class, GAME_ERROR);
    }

    private List<Guess> getPlayerGuesses(String roomId, int roundIndex, int wordIndex){
        String playerGuessString = roundRepository.findPlayerGuessesString(roomId, roundIndex, wordIndex);
        if(playerGuessString == null) return new ArrayList<>();
        return jsonSerializer.deserializeList(playerGuessString, Guess.class, GAME_ERROR);
    }


    /**
     * AI 추측 제출 처리(GUESS_SUBMIT) 이벤트 발행, BROADCAST
     */
    public void handleAIGuessSubmit(String roomId, Round currentRound, Word currentWord, Guess guess) {
        // 0. 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_SUBMIT);

        // 1. 실제 AI 추측 데이터 가져옴
        List<AiPrediction> predictions = getAIPredictions(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        String aiPredicted = predictions.get(guess.getAttempts()-1).getPredicted();

        // 2. attempts에 따라 GUESS 데이터 저장
        guess.setGuessWord(aiPredicted);

        // 3. get AI says
        String aiSays = aiClientService.getGuessMessage(roomId, new AIGuessMessageReq(predictions.get(guess.getAttempts()-1).getPredicted()));

        // 4. AI GUESS Broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_SUBMIT, guess, aiSays);

        // 5. 정답 확인
        guess.setCorrect(aiPredicted.equalsIgnoreCase(currentWord.getWord()));

        // 6. 현재 Word에 guess 추가
        currentWord.getAiGuesses().add(guess);
        List<Guess> aiGuesses = getAIGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        aiGuesses.add(guess);

        String aiGuessesString = jsonSerializer.serialize(aiGuesses, GAME_ERROR);
        roundRepository.saveAIGuessesString(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex(), aiGuessesString);

        // 7. Handle Guess Result
        handleGuessResult(roomId,  currentWord, guess);
    }


    public void handlePlayerGuessSubmit(String roomId, GuessSubmitReq guessSubmitReq, String guesserUuid){
        //0. 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_SUBMIT);



        //1. 현재 Round, Word 가져오기
        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(roomId, getCurrentRound(roomId));

        // 2. 현재 Word의 Drawer == guesser 라면 Exception
        if(currentWord.getDrawerUuid().equals(guesserUuid)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GUESSER);
        }

        //3. BUILD GUESS DATA
        Guess guess = Guess.builder()
                .guesserUuid(guesserUuid)
                .guessWord(guessSubmitReq.guessWord())
                .attempts(currentWord.getPlayerGuesses().size() + 1)
                .build();


        //3. GUESS 정보 Broadcast
        gameBroadCaster.broadcastGameEvent(guesserUuid, roomId, GameEventType.GUESS_SUBMIT, guess, null);


        //4. 현재 PlayerGuess 조회
        List<Guess> playerGuesses = getPlayerGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        currentWord.setPlayerGuesses(playerGuesses);

        //5. 현재 Word에 guess 추가
        currentWord.getPlayerGuesses().add(guess);

        //6. 맞음?
        guess.setCorrect(guess.getGuessWord().equalsIgnoreCase(currentWord.getWord()));

        String playerGuessesJson = jsonSerializer.serialize(playerGuesses, GAME_ERROR);
        roundRepository.savePlayerGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex(), playerGuessesJson);

        //5. handle guess result
        //todo: handlerguessresult() 호출 시에 정답 확인, word에 guess를 추가하는건 어떨까? handlerAIGuessSubmit()과 코드가 중복된 내용이 있음.
        handleGuessResult(roomId, currentWord, guess);
    }


    /**
     * 라운드 종료 처리 (GUESSING_PHASE -> ROUND_ENDED)
     */
    private void handleRoundEnd(String roomId){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.ROUND_END);

        // 상태 업데이트
        gameMeta.setGameStatus(GameStatus.ROUND_ENDED);

        gameMeta.setCurrentRound(gameMeta.getCurrentRound() + 1);

        gameRepository.saveGameMeta(gameMeta);
        if(gameMeta.getCurrentRound() < gameMeta.getTotalRounds()){
            // next round 시작
            roundStartService.startNextRound(roomId);
        } else {
            endGame(roomId);
        }
    }




    /**
     * 게임 종료 처리 (GUESSING_PHASE> GAME_ENDED)
     * Game 데이터 전부 모아서 반환
     */
    private void endGame(String roomId){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GAME_END);

        // 상태 업데이트
        gameMeta.setGameStatus(GameStatus.GAME_ENDED);

        //1. 모든 라운드 메타정보 조회
        List<Round> rounds = getRoundMetas(roomId)
                .stream().map(RoundMeta::toRound).toList();


        for(Round round : rounds){
            //2. 모든 단어 메타정보 조회
            List<Word> words = getWordMetas(roomId, round.getRoundIndex())
                    .stream().map(WordMeta::toWord).toList();

            for(Word word : words){
                //3. 모든 정보 조회 및 연결
                List<Guess> aiGuesses = getAIGuesses(roomId, round.getRoundIndex(), word.getWordIndex());
                List<Guess> playerGuesses = getPlayerGuesses(roomId, round.getRoundIndex(), word.getWordIndex());
                List<AiPrediction> aiPredictions = getAIPredictions(roomId, round.getRoundIndex(), word.getWordIndex());

                word.setAiGuesses(aiGuesses);
                word.setPlayerGuesses(playerGuesses);
                word.setAiPredictions(aiPredictions);
            }
            round.setWords(words);
        }

        // 4. Game 데이터 만들기
        Game game = Game.fromGameMeta(gameMeta);
        game.setRounds(rounds);

        //5. GamePlayers 찾기
        List<GamePlayer> gamePlayers = getGamePlayersByRoomId(roomId);
        game.setGamePlayers(gamePlayers);

        //5. Score 추가, GameWinner 찾기
        determineGameWinnerAndCalculateScores(game);

        //6. GameEnded React 가져오기
        String aiSays = aiClientService.getGameEndMessage(roomId, new AIGameEndReq(game.getPlayerWon() ? "PLAYER" : "AI"));


        //6. GameEnded 이벤트 broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GAME_END, game, aiSays);

        //todo: 7. Game 마무리 : DB 저장


    }


    private List<WordMeta> getWordMetas(String roomId, int roundIndex){
        String wordMetasJson = roundRepository.findWordMetasString(roomId, roundIndex);
        return jsonSerializer.deserializeList(wordMetasJson, WordMeta.class, GAME_ERROR);
    }

    private void saveWordMetas(String roomId, int roundIndex, List<WordMeta> wordMetas){
        String wordMetasJson = jsonSerializer.serialize(wordMetas, GAME_ERROR);
        roundRepository.saveWordMetasString(roomId, roundIndex, wordMetasJson);
    }



    private void determineGameWinnerAndCalculateScores(Game game) {
        //1. 점수 가져오기
        List<Word> words = new ArrayList<>();
        for(Round round : game.getRounds()){
            words.addAll(round.getWords());
        }

        // playerWon = True면 playerScore, False면 aiScore
        int playerScore = 0;
        int aiScore = 0;
        for(Word word : words){
            if(word.getPlayerWon()){
                playerScore += word.getScore();
            } else {
                aiScore += word.getScore();
            }
        }

        game.setPlayerScore(playerScore);
        game.setAiScore(aiScore);
        game.setPlayerWon(playerScore >= aiScore);
    }




    /**
     * 다음 단어로 이동(word index + 1)
     */
    private void moveToNextWord(String roomId, Round currentRound){
        currentRound.setCurrentWordIndex(currentRound.getCurrentWordIndex() + 1);
        List<RoundMeta> roundMetas = getRoundMetas(roomId);
        // currentRound의 currentWordIndex 값을 바꿔서 저장
        roundMetas.get(currentRound.getRoundIndex()).setCurrentWordIndex(currentRound.getCurrentWordIndex());
        roundRepository.saveRoundMetasString(roomId, jsonSerializer.serialize(roundMetas, GAME_ERROR));
        Word currentWord = getCurrentWord(roomId, currentRound);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            gameBroadCaster.broadcastGameEvent("SYSTEM", roomId,GameEventType.GUESS_START,  Word.toWordMeta(currentWord), null);
        }, 5, TimeUnit.SECONDS);
        processNextGuessRequest(roomId);
    }


    /**
     * 추측 결과 처리(CurrentWord와 Guess 비교)
     * GUESS_RESULT 발행, BROADCAST
     *  "data": {
     *     "gameData": {
     * 	    "guesserUuid": "playerB",
     * 	    "attempts": 1,
     * 	    "guessWord": "바나나",
     *       "correct": true,
     *     }
     *     "aiSays" : "이걸 맞추네 ㄷㄷㄷ 이게 어케 바나나임?"
     *   }
     */
    private void handleGuessResult(String roomId, Word currentWord, Guess guess){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_RESULT);

        // guesser 이름 받음
        String guesserUuid = guess.getGuesserUuid();
        String guesserJson = gamePlayerRepository.findGamePlayerStringByUuid(roomId, guesserUuid);
        GamePlayer gamePlayer = jsonSerializer.deserialize(guesserJson, GamePlayer.class, GAME_ERROR);
        String guesser = guesserUuid.equals("AI") ? "묘묘" : gamePlayer.getNickname();
        
        // AI 반응 받음
        String aiSays = aiClientService.getGuessReactMessage(roomId, new AIGuessReactReq(guess.getCorrect(), currentWord.getWord(), guesser));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            // GUESS RESULT Broadcast
            gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_RESULT, guess, aiSays);
        }, 5, TimeUnit.SECONDS);

        // 다음 턴
        processNextGuessRequest(roomId);
    }




    /**
     * 추측 종료 후 점수 업데이트
     */
    private void handleBattleEnd(String roomId, Round currentRound, Word currentWord){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.BATTLE_END);
        log.info("currentRound: {}", currentRound);
        // 1. currentWord 에서 Guess 가져오기
        List<Guess> playerGuesses = getPlayerGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        List<Guess> aiGuesses = getAIGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        log.info("PlayerGuesses: {}", playerGuesses.toString());
        log.info("AIGuesses: {}", aiGuesses.toString());

        // 2. Guess 정답 된 guess 가져오기, 정답이 없으면?
        boolean isPlayerWin = true;
        Guess guess = null;
        for(Guess playerGuess : playerGuesses){
            if(playerGuess.getCorrect()) {
                guess = playerGuess;
                break;
            }
        }
        for(Guess aiGuess : aiGuesses){
            if(aiGuess.getCorrect()) {
                guess = aiGuess;
                isPlayerWin = false;
                break;
            }
        }

        //3. WordMeta 값 바꾸기
        List<WordMeta> wordMetas = getWordMetas(roomId, currentRound.getRoundIndex());
        wordMetas.get(currentWord.getWordIndex()).setPlayerWon(isPlayerWin);

        // guess == null이면 정답 없음.
        if(guess == null) {
            wordMetas.get(currentWord.getWordIndex()).setScore(0);
        }
        else {
            int newScore = 10 * (3 - guess.getAttempts() + 1);
            wordMetas.get(currentWord.getWordIndex()).setScore(newScore);
        }

        saveWordMetas(roomId, currentRound.getRoundIndex(), wordMetas);

        //4. WordMeta 전체 가져오기
        List<Boolean> allPlayerWons = new ArrayList<>();
        for(int i = 0; i < gameMeta.getTotalRounds(); i++) {
            List<WordMeta> roundWordMetas = getWordMetas(roomId, i);
            for(int j = 0; j < 2; j++){
                allPlayerWons.add(roundWordMetas.get(j).getPlayerWon());
            }
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.BATTLE_END, allPlayerWons, null);
        }, 5, TimeUnit.SECONDS);
    }

    private GameMeta getGameMetaByRoomId(String roomId) {
        Map<Object, Object> gameMetaMap = gameRepository.findGameMeta(roomId);
        if(gameMetaMap.isEmpty()) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_ID);
        }
        return GameMeta.fromRedisMap(roomId, gameMetaMap);
    }

    private void validateGameEvent(GameMeta gameMeta, GameEventType gameEventType) {
        if(!gameMeta.getGameStatus().canHandleEvent(gameEventType)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_STATUS);
        }
    }


    private List<GamePlayer> getGamePlayersByRoomId(String roomId) {
        List<String> playerUuids = gamePlayerRepository.findPlayerUuidsByRoomId(roomId);
        return playerUuids.stream()
                .map(uuid -> gamePlayerRepository.findGamePlayerStringByUuid(roomId, uuid))
                .map(gamePlayerJson -> jsonSerializer.deserialize(gamePlayerJson, GamePlayer.class, GAME_ERROR))
                .collect(Collectors.toList());
    }

    /**
     * 다음 추측자는 누구?
     * attempts 지금까지 몇 번 했는지 확인
     */
    private boolean determineNextGuesser(Word word){
        return word.getAiGuesses().size() == word.getPlayerGuesses().size(); // AI가 먼저 시작, 번갈아가며 진행
    }

    private List<RoundMeta> getRoundMetas(String roomId) {
        String roundsJson = roundRepository.findRoundMetasString(roomId);
        return jsonSerializer.deserializeList(roundsJson, RoundMeta.class, GAME_ERROR);
    }

    /**
     * 단어 추측 완료 여부 확인
     */
    private boolean isWordGuessCompleted(Word word) {
        // 1. 누군가 맞췄는지 확인
        boolean hasCorrectGuess = word.getAiGuesses().stream().anyMatch(Guess::getCorrect) ||
                word.getPlayerGuesses().stream().anyMatch(Guess::getCorrect);
        if (hasCorrectGuess) return true;

        // 2. AI와 플레이어 모두 3번씩 시도했는지 확인
        int aiAttempts = word.getAiGuesses().size();
        int playerAttempts = word.getPlayerGuesses().size();

        return aiAttempts >= 3 && playerAttempts >= 3;
    }


    /**
     * 현재 추측할 단어 가져오기
     */
    private Word getCurrentWord(String roomId, Round round) {
        if (round.getCurrentWordIndex() >= round.getWords().size()) {
            return null; // 모든 단어 완료
        }

        List<Guess> playerGuesses = getPlayerGuesses(roomId, round.getRoundIndex(), round.getCurrentWordIndex());
        List<Guess> aiGuesses = getAIGuesses(roomId, round.getRoundIndex(), round.getCurrentWordIndex());

        Word word = round.getWords().get(round.getCurrentWordIndex());
        word.setPlayerGuesses(playerGuesses);
        word.setAiGuesses(aiGuesses);

        return word;
    }


    private RoundMeta getCurrentRoundMeta(String roomId) {
        // 0. GameMeta 조회
        GameMeta gameMeta = getGameMetaByRoomId(roomId);

        // 1. gameMeta 조회 후 현재 라운드 index 받기
        int roundIndex = gameMeta.getCurrentRound();

        return getRoundMetas(roomId).get(roundIndex);
    }


    private Round getCurrentRound(String roomId) {
        // 1. Round 메타정보 조회
        RoundMeta roundMeta = getCurrentRoundMeta(roomId);

        // 2. Words 메타정보 조회
        List<WordMeta> wordMetas = getWordMetas(roomId, roundMeta.getRoundIndex());

        // 3. 데이터 파싱 후 결합
        List<Word> words = wordMetas.stream().map(WordMeta::toWord).toList();

        Round round = RoundMeta.toRound(roundMeta);
        round.setWords(words);



        return round;
    }
}
