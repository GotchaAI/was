package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.AIGuessMessageReq;
import socket_server.domain.game.dto.AIGuessReactReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.AiPrediction;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * GUESS_START (ENTRY_POINT)
     * DRAWING_PHASE -> GUESSING_PHASE
     */
    public void startGuessingPhase(String roomId) {
        // 0. 게임 메타정보 조회 -> GameStatus 업데이트
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_START)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }
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
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_REQUEST)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }
        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(currentRound);

        if(currentWord == null){
            // todo: next round
            return;
        }

        if(isWordGuessCompleted(currentWord)){
            // todo: next word
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


    /**
     * AI 추측 제출 처리(GUESS_SUBMIT) 이벤트 발행, BROADCAST
     * "data": {
     *     "gameData": {
     * 	    "guesserUuid": "AI",
     * 	    "attempts" : 1,
     *       "guessWord": "사과",
     * 	    "correct": null // 아직 정답여부 나오지 않음
     *     },
     *     "aiSays" : "우웅, 감이 와! '사과' 맞지? 내 추측이 맞다면 너에게 천재적 감각을 인정해줄게! 😉🌻✨"
     */
    public void handleAIGuessSubmit(String roomId, Round currentRound, Word currentWord, Guess guess) {
        // 0. 상태 검증
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_SUBMIT)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        // 1. 실제 AI 추측 데이터 가져옴
        List<AiPrediction> predictions = roundRepository.findAIPredictions(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex());
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
        roundRepository.addAIGuess(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex(), guess);

        // 7. Handle Guess Result
        handleGuessResult(roomId, currentWord.getWord(), guess);
    }


    public void handlePlayerGuessSubmit(String roomId, Guess guess, String guesserUuid){
        //0. 상태 검증
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_SUBMIT)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        //1. GUESS 정보 Broadcast
        gameBroadCaster.broadcastGameEvent(guesserUuid, roomId, GameEventType.GUESS_SUBMIT, guess, null, null);

        //2. Handle Guess Result
        handleGuessResult(roomId, guess.getGuessWord(), guess);

        Round currentRound = getCurrentRound(roomId);
        Word currentWord = getCurrentWord(getCurrentRound(roomId));


        //3. 정답 확인
        guess.setCorrect(guess.getGuessWord().equalsIgnoreCase(currentWord.getWord()));

        //4. 현재 Word에 guess 추가
        currentWord.getPlayerGuesses().add(guess);
        roundRepository.addPlayerGuess(roomId, currentRound.getRoundIndex(), currentWord.getWordIndex(), guess);

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
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.ROUND_END)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        if(gameMeta.getCurrentRound() <= gameMeta.getTotalRounds()){
            // 상태 업데이트
            gameMeta.setGameStatus(GameStatus.ROUND_ENDED);
            gameRepository.saveGameMeta(gameMeta);

            // 현재 ROUND 정보 모으기
            Round currentRound = getCurrentRound(roomId);
            List<Word> words = roundRepository.findWordMetas(roomId, currentRound.getRoundIndex()).stream().map(WordMeta::toWord).toList();

            for(Word word : words){
                List<Guess> aiGuesses = roundRepository.findAIGuesses(roomId, currentRound.getRoundIndex(), word.getWordIndex());
                List<Guess> playerGuesses = roundRepository.findPlayerGuesses(roomId, currentRound.getRoundIndex(), word.getWordIndex());
                List<AiPrediction> aiPredictions = roundRepository.findAIPredictions(roomId, currentRound.getRoundIndex(), word.getWordIndex());

                word.setAiGuesses(aiGuesses);
                word.setPlayerGuesses(playerGuesses);
                word.setAiPredictions(aiPredictions);
            }
            currentRound.setWords(words);

            // ROUND_END 이벤트 발행, 현재 라운드 정보 broadcast
            gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.ROUND_END, currentRound, null, null);

            // next round 시작
            // todo: 일정 시간 기다렸다가?
            roundStartService.startNextRound(roomId);

        } else {
            //todo: 게임 종료
            //endGame(roomId);
        }
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
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.GUESS_RESULT)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        // guesser 이름 받음
        String guesserUuid = guess.getGuesserUuid();
        String guesser = guesserUuid.equals("AI") ? "묘묘" : gamePlayerRepository.findPlayerByUuid(roomId, guesserUuid).getNickname();
        
        // AI 반응 받음
        String aiSays = aiClientService.getGuessReactMessage(roomId, new AIGuessReactReq(guess.getCorrect(), currentWord, guesser));

        // GUESS RESULT Broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_RESULT, guess, aiSays, null);

        if(guess.getCorrect()){
            // GUESS 성공. attempts와 함께 점수 업데이트
            updateScore(roomId, guess);
        } else {
            // 다음 턴 (GUESS 실패)
            processNextGuessRequest(roomId);
        }

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
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.SCORE_UPDATE)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        String guesserUuid = guess.getGuesserUuid();

        // 점수 업데이트
        int currentScore = gamePlayerRepository.findScoreByUuid(roomId, guesserUuid);
        int newScore = currentScore + guess.getAttempts() * (3 - guess.getAttempts() + 1);

        gamePlayerRepository.saveScoreByUuid(roomId, guesserUuid, newScore);

        // SCORE_UPDATE Broadcast
        Map<String, Integer> scores = new HashMap<>();
        scores.put("AI", gamePlayerRepository.findScoreByUuid(roomId, "AI"));
        scores.put("playerA", gamePlayerRepository.findScoreByUuid(roomId, "playerA"));
        scores.put("playerB", gamePlayerRepository.findScoreByUuid(roomId, "playerB"));
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.SCORE_UPDATE, scores, null, null);
    }


    /**
     * 다음 추측자는 누구?
     * attempts 지금까지 몇 번 했는지 확인
     */
    private boolean determineNextGuesser(Word word){
        return word.getAiGuesses().size() == word.getPlayerGuesses().size(); // AI가 먼저 시작, 번갈아가며 진행
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

    private int getCurrentRoundIndex(String roomId){
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        return gameMeta.getCurrentRound();
    }

    private Round getCurrentRound(String roomId) {
        // 1. gameMeta 조회 후 현재 라운드 index 받기
        int roundIndex = getCurrentRoundIndex(roomId);

        // 2. Round 메타정보 조회
        RoundMeta roundMeta = roundRepository.findRoundMetas(roomId).get(roundIndex - 1);

        // 3. Words 메타정보 조회
        List<WordMeta> wordMetas = roundRepository.findWordMetas(roomId, roundIndex);

        // 4. 데이터 파싱 후 결합
        List<Word> words = wordMetas.stream().map(WordMeta::toWord).toList();
        Round round = RoundMeta.toRound(roundMeta);
        round.setWords(words);

        return round;
    }
}
