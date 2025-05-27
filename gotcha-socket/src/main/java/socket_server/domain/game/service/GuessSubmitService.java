package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.domain.game.dto.AIGuessMessageReq;
import socket_server.domain.game.dto.AISaysRes;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.model.AiPrediction;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.RoundRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuessSubmitService {


    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final AIClientService aiClientService;


    public String submitGuessAI(String roomId, Round currentRound, Word guessTargetWord, Guess guess){
        // 1. AI PREDICTION 가져와서
        List<AiPrediction> aiPredictions = roundRepository.findAIPredictions(roomId, currentRound.getRoundIndex(), guessTargetWord.getWordIndex());

        // 2. attempts에 따라 GUESS 데이터 저장
        guess.setGuessWord(aiPredictions.get(guess.getAttempts()-1).getPredicted());

        // 2. 현재 Word 에 Guess 추가
        guessTargetWord.getAiGuesses().add(guess);

        //3. Guess 저장
        roundRepository.addAIGuess(roomId, currentRound.getRoundIndex(),guessTargetWord.getWordIndex(), guess);

        //4. get ai says
        String aiSays = aiClientService.getGuessMessage(roomId, new AIGuessMessageReq(aiPredictions.get(guess.getAttempts()-1).getPredicted()));

        //5. AI GUESS Broadcast
        gameBroadCaster.broadcastGameEvent("SYSTEM", roomId, GameEventType.GUESS_SUBMIT, guess, aiSays, null);

        return guess.getGuessWord();
    }


}
