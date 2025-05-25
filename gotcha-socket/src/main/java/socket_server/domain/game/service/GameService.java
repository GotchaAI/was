package socket_server.domain.game.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
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

    private final RoomService roomService;
    private final RoomUserRepository roomUserRepository;
    private final GameRepository gameRepository;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;

    public void startGame(String roomId, String userUuid) {
        // 1. 방 데이터 조회 및 호스트 검증
        RoomMetadata roomMetadata = roomService.getHostingRoomMetadata(roomId, userUuid);

        // 2. 게임 시작 가능한지 확인(레디 상태, 플레이어 수)
        roomService.checkGameStartable(roomId, roomMetadata.getGameType());

        // 3. 게임 메타데이터 생성
        Game game = initGame(roomId, roomMetadata);

        // 4. 게임 플레이어 정보 조회 후 연결
        List<GamePlayer> gamePlayers = getGamePlayersFromRoom(roomId);
        game.setGamePlayers(gamePlayers);

        // 5. 라운드 정보 초기화 후 연결
        List<Round> rounds = initRounds(game.getTotalRounds(), gamePlayers);
        game.setRounds(rounds);

        // 6. Redis에 저장 : GameMeta, GamePlayers, Rounds
        gameRepository.saveGameMeta(game);
        gameRepository.savePlayers(game.getRoomId(), game.getGamePlayers());
        gameRepository.saveRoundMetas(game.getRoomId(), game.getRounds());

        //todo: 7. AI 서버 메시지 받아오기

        // 8. 시작 이벤트 브로드캐스트
        broadcastStartEvent(userUuid, roomId, game);

    }

    private Game initGame(String roomId, RoomMetadata roomMetadata) {
        return Game.builder().
                roomId(roomId).
                gameType(roomMetadata.getGameType()).
                difficulty(roomMetadata.getDifficulty()).
                currentRound(1).
                totalRounds(roomMetadata.getRoundCount()).build();
    }


    private List<Round> initRounds(int totalRounds, List<GamePlayer> gamePlayers) {
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

    private List<GamePlayer> getGamePlayersFromRoom(String roomId) {
        return  roomUserRepository.findUsersByRoomId(roomId).stream().map(RoomUserInfo::toGamePlayer).toList();
    }

    private void broadcastStartEvent(String userUuid, String roomId, Game game) {
        EventRes eventRes = new EventRes(
                EventType.START,
                game,
                LocalDateTime.now()
        );

        RedisMessage redisMessage = new RedisMessage(
                userUuid,
                ROOM_EVENT + roomId,
                jsonSerializer.serialize(eventRes)
        );

        objectRedisTemplate.convertAndSend(ROOM_EVENT + roomId, jsonSerializer.serialize(redisMessage));
    }


}
