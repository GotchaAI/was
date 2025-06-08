package socket_server.gamehistory.service;


import gotcha_domain.gamehistory.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import socket_server.domain.game.model.Guess;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.model.AIPrediction;
import socket_server.gamehistory.repository.RoundHistoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundHistoryService {

    private final RoundHistoryRepository roundHistoryRepository;

    @Transactional
    public RoundHistory createRoundHistory(GameHistory gameHistory, Round round) {
        List<WordInfo> wordInfos = new ArrayList<>(round.getWords().size());
        for(Word word: round.getWords()) {
            List<GuessInfo> playerGuesses = word.getPlayerGuesses().stream()
                    .map(Guess::fromGuess)
                    .toList();

            List<GuessInfo> aiGuesses = word.getAiGuesses().stream()
                    .map(Guess::fromGuess)
                    .toList();

            List<AIPredictionInfo> aiPredictions = word.getAIPredictions().stream()
                    .map(AIPrediction::fromAIPrediction)
                    .toList();

            WordInfo wordInfo = WordInfo.builder()
                    .wordIndex(word.getWordIndex())
                    .word(word.getWord())
                    .drawerUuid(word.getDrawerUuid())
                    .submitted(word.isSubmitted())
                    .imageUrl(word.getImageURL())
                    .playerGuesses(playerGuesses)
                    .aiGuesses(aiGuesses)
                    .aiPredictions(aiPredictions)
                    .build();

            wordInfos.add(wordInfo);
        }

        RoundHistory roundHistory = RoundHistory.builder()
                .gameHistory(gameHistory)
                .roundIndex(round.getRoundIndex())
                .words(wordInfos)
                .build();

        return roundHistoryRepository.save(roundHistory);
    }


}
