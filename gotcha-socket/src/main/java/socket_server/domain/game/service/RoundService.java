package socket_server.domain.game.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.util.WordUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundService {
    /**
     * 도메인 데이터 Round 처리 담당.
     */
    private final GameRepository gameRepository;

    public List<Round> initRounds(int totalRounds, List<GamePlayer> gamePlayers) {
        List<Round> rounds = new ArrayList<>();
        List<Integer> indexes = WordUtils.getRandomIndexes(totalRounds * 2); // get random indexes, 플레이어는 항상 2명이라고 가정
        for(int i = 0; i < totalRounds; i++) {
            List<Word> words = new ArrayList<>();
            for(int j = 0; j < 2; j++){
                Word word = Word.builder()
                        .wordIndex(j)
                        .word(WordUtils.getEngWord(indexes.get(i * 2 + j)))
                        .drawerUuid(gamePlayers.get(j).getPlayerUuid())
                        .guesses(new ArrayList<>())
                        .aiPredictions(new ArrayList<>()).build();
                words.add(word);
            }

            Round round = Round.builder().
                    roundIndex(i).
                    words(words).
                    build();
            rounds.add(round);
        }
        return rounds;
    }


}
