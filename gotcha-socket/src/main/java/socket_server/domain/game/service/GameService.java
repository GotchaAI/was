package socket_server.domain.game.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.util.IDGenerator;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.util.WordUtils;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomUserRepository;
import socket_server.domain.room.service.RoomService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GameService {
    /**
     * 1. 게임 시작
     */
    private final IDGenerator idGenerator;
    private final RoomService roomService;
    private final RoomUserRepository roomUserRepository;

    public void startGame(String roomId, String userUuid, int totalRounds) {
        // 1. host id check, 방 데이터 가져오기
        RoomMetadata roomMetadata = roomService.getHostingRoomMetadata(roomId, userUuid);

         // 2. Game 데이터 만들기
        Game game = new Game(
                idGenerator.allocateGameId(), // gameUUID
                roomMetadata.getGameType(), // GameType
                roomMetadata.getDifficulty(), // Difficulty
                totalRounds, // totalRounds
                0, // aiScore
                null, // gamePlayers
                null, // rounds
                null // winner
        );

        // 3. GamePlayerList 가져오기
        List<GamePlayer> gamePlayers = roomUserRepository.findUsersByRoomId(roomId).stream().map(RoomUserInfo::toGamePlayer).toList();
        game.setGamePlayers(gamePlayers);

        // 4. Round, Word 데이터 만들기
        List<Round> rounds = new ArrayList<>();

        List<Integer> indexes = WordUtils.getRandomIndexes(game.getTotalRounds() * 2); // get random indexes, 플레이어는 항상 2명이라고 가정

        for(int i = 0; i < game.getTotalRounds(); i++) {
            List<Word> words = new ArrayList<>();
            for(int j = 0; j < 2; j++){
                Word word = new Word(
                        indexes.get(j),
                        WordUtils.getEngWord(indexes.get(i * 2 + j)),
                        gamePlayers.get(j).getPlayerUuid(),
                        new ArrayList<>(),
                        new ArrayList<>()
                );
                words.add(word);
            }

            Round round = new Round(
                    i,
                    null,
                    null,
                    words
            );
            rounds.add(round);
        }

        game.setRounds(rounds);

        // 5. Redis에 저장




    }



}
