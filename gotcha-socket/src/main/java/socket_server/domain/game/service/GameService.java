package socket_server.domain.game.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.util.IDGenerator;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.util.WordUtils;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomUserRepository;
import socket_server.domain.room.service.RoomService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static socket_server.common.constants.WebSocketConstants.ROOM_EVENT;

@AllArgsConstructor
@Service
public class GameService {

    private final IDGenerator idGenerator;
    private final RoomService roomService;
    private final RoomUserRepository roomUserRepository;
    private final GameRepository gameRepository;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;

    public void startGame(String roomId, String userUuid, int totalRounds) {
        // 1. host id check, 방 데이터 가져오기
        RoomMetadata roomMetadata = roomService.getHostingRoomMetadata(roomId, userUuid);

         // 2. Game 데이터 만들기
        Game game = Game.builder().
                gameId(idGenerator.allocateGameId()).
                gameType(roomMetadata.getGameType()).
                difficulty(roomMetadata.getDifficulty()).
                currentRound(1).
                totalRounds(totalRounds).build();

        // 3. GamePlayerList 가져오기
        List<GamePlayer> gamePlayers = roomUserRepository.findUsersByRoomId(roomId).stream().map(RoomUserInfo::toGamePlayer).toList();
        game.setGamePlayers(gamePlayers);

        // 4. Round, Word 데이터 만들기
        List<Round> rounds = new ArrayList<>();
        List<Integer> indexes = WordUtils.getRandomIndexes(game.getTotalRounds() * 2); // get random indexes, 플레이어는 항상 2명이라고 가정
        for(int i = 0; i < game.getTotalRounds(); i++) {
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

        game.setRounds(rounds);

        // 5. Redis에 저장 : GameMeta, GamePlayers, Rounds
        gameRepository.saveGameMeta(game);
        gameRepository.savePlayers(game.getGameId(), game.getGamePlayers());
        gameRepository.saveRoundMetas(game.getGameId(), game.getRounds());

        //todo: AI 서버 메시지 받아오기

        EventRes eventRes = new EventRes(
                EventType.START,
                game,
                LocalDateTime.now()
        );

        RedisMessage redisMessage = new RedisMessage(
                userUuid,
                ROOM_EVENT+roomId,
                jsonSerializer.serialize(eventRes));

        objectRedisTemplate.convertAndSend(ROOM_EVENT+roomId, jsonSerializer.serialize(redisMessage));

    }



}
