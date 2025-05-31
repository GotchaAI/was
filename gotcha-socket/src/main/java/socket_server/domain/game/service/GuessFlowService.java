package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.AIGameEndReq;
import socket_server.domain.game.dto.AIGuessMessageReq;
import socket_server.domain.game.dto.AIGuessReactReq;
import socket_server.domain.game.dto.AIRoundEndReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.*;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // 1. 라운드 데이터 전체 조회
        Round currentRound = getCurrentRound(roomId);

        // 2. WordMeta BroadCast (현재 라운드에 대해서)
        // GUESS_START 이벤트 발행
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_START,
                currentRound.getWords().stream().map(Word::toWordMeta).toList(), null, null);


        processNextGuessRequest(roomId);
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

        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(currentRound);

        if(currentWord == null){
            handleRoundEnd(roomId);
            return;
        }
        // Guess 데이터 찾아서 넣어주고
        List<Guess> playerGuesses = getPlayerGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        currentWord.setPlayerGuesses(playerGuesses);

        List<Guess> aiGuesses = getAIGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        currentWord.setAiGuesses(aiGuesses);

        if(isWordGuessCompleted(currentWord)){
            moveToNextWord(roomId, currentRound);
            return;
        }

        boolean isAITurn = determineNextGuesser(currentWord);

        if(isAITurn){ // next guess
            Guess newGuess = guessRequestService.requestGuessAI(roomId, gameMeta, currentWord);
            handleAIGuessSubmit(roomId, currentRound, currentWord, newGuess);
        } else {
            guessRequestService.requestGuessPlayer(roomId, gameMeta, currentWord);
        }

    }

    private List<AiPrediction> getAIPredictions(String roomId, int roundIndex, int wordIndex){
        String aiPredictionString = roundRepository.findAIPredictionsString(roomId, roundIndex, wordIndex);
        return jsonSerializer.deserializeList(aiPredictionString, AiPrediction.class, GAME_ERROR);
    }



    private List<Guess> getAIGuesses(String roomId, int roundIndex, int wordIndex){
        String aiGuessString = roundRepository.findAIGuessesString(roomId, roundIndex, wordIndex);
        return jsonSerializer.deserializeList(aiGuessString, Guess.class, GAME_ERROR);
    }

    private List<Guess> getPlayerGuesses(String roomId, int roundIndex, int wordIndex){
        String playerGuessString = roundRepository.findPlayerGuessesString(roomId, roundIndex, wordIndex);
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
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_SUBMIT, guess, aiSays, null);

        // 5. 정답 확인
        guess.setCorrect(aiPredicted.equalsIgnoreCase(currentWord.getWord()));

        // 6. 현재 Word에 guess 추가
        currentWord.getAiGuesses().add(guess);
        List<Guess> aiGuesses = getAIGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        aiGuesses.add(guess);

        String aiGuessesString = jsonSerializer.serialize(aiGuesses, GAME_ERROR);
        roundRepository.saveAIGuessesString(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex(), aiGuessesString);

        // 7. Handle Guess Result
        handleGuessResult(roomId, currentWord.getWord(), guess);
    }


    public void handlePlayerGuessSubmit(String roomId, Guess guess, String guesserUuid){
        //0. 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_SUBMIT);

        //1. GUESS 정보 Broadcast
        gameBroadCaster.broadcastGameEvent(guesserUuid, roomId, GameEventType.GUESS_SUBMIT, guess, null, null);

        //2. 현재 Round, Word 가져오기
        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(getCurrentRound(roomId));

        // 3. 현재 Word의 Drawer == guesser 라면 Exception
        if(currentWord.getDrawerUuid().equals(guesserUuid)) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GUESSER);
        }


        List<Guess> playerGuesses = getPlayerGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
        currentWord.setPlayerGuesses(playerGuesses);


        guess.setCorrect(guess.getGuessWord().equalsIgnoreCase(currentWord.getWord()));

        //4. 현재 Word에 guess 추가
        currentWord.getPlayerGuesses().add(guess);
        String playerGuessesJson = jsonSerializer.serialize(playerGuesses, GAME_ERROR);
        roundRepository.savePlayerGuesses(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex(), playerGuessesJson);

        //5. handle guess result
        //todo: handlerguessresult() 호출 시에 정답 확인, word에 guess를 추가하는건 어떨까? handlerAIGuessSubmit()과 코드가 중복된 내용이 있음.
        handleGuessResult(roomId, currentWord.getWord(), guess);
    }


    /**
     * 라운드 종료 처리 (GUESSING_PHASE -> ROUND_ENDED)
     * ROUND_END 발행, BROADCAST
     */
    private void handleRoundEnd(String roomId){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.ROUND_END);

        // 상태 업데이트
        gameMeta.setGameStatus(GameStatus.ROUND_ENDED);
        gameRepository.saveGameMeta(gameMeta);

        // 현재 ROUND 정보 모으기
        Round currentRound = getCurrentRound(roomId);
        List<Word> words = getWordMetas(roomId, currentRound.getRoundIndex()).stream().map(WordMeta::toWord).toList();

        for(Word word : words){
            List<Guess> aiGuesses = getAIGuesses(roomId, currentRound.getRoundIndex(), word.getWordIndex());
            List<Guess> playerGuesses = getPlayerGuesses(roomId, currentRound.getRoundIndex(), word.getWordIndex());
            List<AiPrediction> aiPredictions = getAIPredictions(roomId, currentRound.getRoundIndex(), word.getWordIndex());

            word.setAiGuesses(aiGuesses);
            word.setPlayerGuesses(playerGuesses);
            word.setAiPredictions(aiPredictions);
        }
        currentRound.setWords(words);


        // 점수 업데이트 된걸로 라운드 winner 찾기
        determineRoundWinner(roomId, currentRound);

        //RoundWinner에 따른 AI 반응 메시지 추가(aiSays)
        String aiSays = aiClientService.getRoundEndMessage(roomId, new AIRoundEndReq(currentRound.getRoundIndex(), gameMeta.getTotalRounds(), currentRound.getRoundWinner()));

        // ROUND_END 이벤트 발행, 현재 라운드 정보 broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.ROUND_END, currentRound, aiSays, null);

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



        //5. Score 추가, GameWinner 찾기
        determineGameWinnerAndCalculateScores(roomId, game);

        //6. GameEnded React 가져오기
        String aiSays = aiClientService.getGameEndMessage(roomId, new AIGameEndReq(game.getWinner()));



        //6. GameEnded 이벤트 broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GAME_END, game, aiSays, null);

        //todo: 7. Game 마무리 : DB 저장


    }


    private List<WordMeta> getWordMetas(String roomId, int roundIndex){
        String wordMetasJson = roundRepository.findWordMetasString(roomId, roundIndex);
        return jsonSerializer.deserializeList(wordMetasJson, WordMeta.class, GAME_ERROR);
    }



    private void determineGameWinnerAndCalculateScores(String roomId, Game game) {
        //0. Score Map 초기 설정
        Map<String, Integer> scores = new HashMap<>();
        List<String> playerUuids = gamePlayerRepository.findPlayerUuidsByRoomId(roomId);
        for(String playerUuid : playerUuids){
            scores.put(playerUuid, 0);
        }
        scores.put("AI", 0);
        game.setScores(scores);


        //1. 점수 가져오기
        List<Round> rounds = game.getRounds();
        for(Round round : rounds){
            Map<String, Integer> roundScores = round.getRoundScores();
            int aiRoundScore = roundScores.getOrDefault("AI", 0); // AI Score of this round
            int aiGameScore = scores.getOrDefault("AI", 0); // AI Score of whole game
            scores.put("AI", aiGameScore + aiRoundScore);
            for(String playerUuid : roundScores.keySet()){
                if(!playerUuid.equals("AI")) {
                    int playerRoundScore = roundScores.getOrDefault(playerUuid, 0);
                    int playerGameScore = scores.getOrDefault(playerUuid, 0);
                    scores.put(playerUuid, playerGameScore + playerRoundScore);
                }
            }
        }

        //2. GameWinner 구하기
        int aiScore = scores.getOrDefault("AI", 0);
        int playerScore = scores.getOrDefault("PLAYER", 0);
        String gameWinner = aiScore > playerScore ? "AI" : aiScore == playerScore ? "DRAW" : "PLAYER";
        game.setWinner(gameWinner);


        //3. 최종점수 저장
        game.setScores(scores);


    }


    private void determineRoundWinner(String roomId, Round currentRound){
        // 1. 점수 가져오기
        Map<String, Integer> scores = gamePlayerRepository.findRoundScores(roomId, currentRound.getRoundIndex());

        //2. 점수 비교
        int aiScore = 0;
        int playerScore = 0;
        for(String playerUuid : scores.keySet()){
            if(playerUuid.equals("AI")) aiScore += scores.get(playerUuid);
            else playerScore += scores.get(playerUuid);
        }

        //3. RoundWinner 구하기
        String roundWinner = aiScore > playerScore ?
                "AI" : aiScore == playerScore ? "DRAW" : "PLAYER";
        currentRound.setRoundWinner(roundWinner);


    }



    /**
     * 다음 단어로 이동(word index + 1)
     */
    private void moveToNextWord(String roomId, Round currentRound){
        currentRound.setCurrentWordIndex(currentRound.getCurrentWordIndex() + 1);
        List<RoundMeta> roundMetas = getRoundMetas(roomId);
        // currentRound의 currentWordIndex 값을 바꿔서 저장
        roundMetas.get(currentRound.getRoundIndex() - 1).setCurrentWordIndex(currentRound.getCurrentWordIndex());
        roundRepository.saveRoundMetasString(roomId, jsonSerializer.serialize(roundMetas, GAME_ERROR));

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
    private void handleGuessResult(String roomId, String currentWord, Guess guess){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.GUESS_RESULT);

        // guesser 이름 받음
        String guesserUuid = guess.getGuesserUuid();
        String guesserJson = gamePlayerRepository.findGamePlayerStringByUuid(roomId, guesserUuid);
        GamePlayer gamePlayer = jsonSerializer.deserialize(guesserJson, GamePlayer.class, GAME_ERROR);
        String guesser = guesserUuid.equals("AI") ? "묘묘" : gamePlayer.getNickname();
        
        // AI 반응 받음
        String aiSays = aiClientService.getGuessReactMessage(roomId, new AIGuessReactReq(guess.getCorrect(), currentWord, guesser));

        // GUESS RESULT Broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_RESULT, guess, aiSays, null);

        if(guess.getCorrect()){
            // GUESS 성공. attempts와 함께 점수 업데이트
            updateScore(roomId, guess);
        }
        // 다음 턴 (GUESS 실패)
        processNextGuessRequest(roomId);
    }


    /**
     * 추측 종료 후 점수 업데이트
     * SCORE_UPDATE 이벤트 발행
     *     "scores": {
     *       "AI": 30,
     *       "playerA": 0,
     *       "playerB": 0
     *     }
     */
    private void updateScore(String roomId, Guess guess){
        // 상태 검증
        GameMeta gameMeta = getGameMetaByRoomId(roomId);
        validateGameEvent(gameMeta, GameEventType.SCORE_UPDATE);

        //1. current ROund Index
        List<RoundMeta> roundMetas = getRoundMetas(roomId);

        //2. List<RoundMeta>
        RoundMeta currentRoundMeta = roundMetas.get(gameMeta.getCurrentRound() - 1);



        String guesserUuid = guess.getGuesserUuid();

        // 점수 업데이트
        int currentScore = gamePlayerRepository.findRoundScoreByUuid(roomId, guesserUuid, gameMeta.getCurrentRound());
        int newScore = currentScore + guess.getAttempts() * (3 - guess.getAttempts() + 1);

        gamePlayerRepository.saveRoundScoreByUuid(roomId, guesserUuid, gameMeta.getCurrentRound(), newScore);

        // SCORE_UPDATE Broadcast
        List<GamePlayer> gamePlayers = getGamePlayersByRoomId(roomId);
        String playerA = gamePlayers.get(0).getPlayerUuid();
        String playerB = gamePlayers.get(1).getPlayerUuid();
        Map<String, Integer> scores = new HashMap<>();
        scores.put("AI", gamePlayerRepository.findRoundScoreByUuid(roomId, "AI", gameMeta.getCurrentRound()));
        scores.put(playerA, gamePlayerRepository.findRoundScoreByUuid(roomId, playerA, gameMeta.getCurrentRound()));
        scores.put(playerB, gamePlayerRepository.findRoundScoreByUuid(roomId, playerB, gameMeta.getCurrentRound()));
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.SCORE_UPDATE, scores, null, null);

        currentRoundMeta.setRoundScores(scores);
        String roundMetasJson = jsonSerializer.serialize(roundMetas, GAME_ERROR);
        roundRepository.saveRoundMetasString(roomId, roundMetasJson);

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
    private Word getCurrentWord(Round round) {
        if (round.getCurrentWordIndex() >= round.getWords().size()) {
            return null; // 모든 단어 완료
        }
        return round.getWords().get(round.getCurrentWordIndex());
    }


    private RoundMeta getCurrentRoundMeta(String roomId) {
        // 0. GameMeta 조회
        GameMeta gameMeta = getGameMetaByRoomId(roomId);

        // 1. gameMeta 조회 후 현재 라운드 index 받기
        int roundIndex = gameMeta.getCurrentRound();

        return getRoundMetas(roomId).get(roundIndex - 1);
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
