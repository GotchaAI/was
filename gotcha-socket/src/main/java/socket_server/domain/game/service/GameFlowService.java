package socket_server.domain.game.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
/**
 * 게임 전체 흐름 담당.
 */
@RequiredArgsConstructor
@Service
public class GameFlowService {


    private final GameService gameService;
    private final GamePlayerService gamePlayerService;
    private final RoundService roundService;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;

    public void startGame(String roomId, String userUuid) {
        // 1. 게임 시작 가능한지(레디 상태, 플레이어 수) check 후 방 메타정보 조회
        RoomMetadata roomMetadata = gameService.validateGameStart(roomId, userUuid);
        
        // 2. 게임 메타데이터 생성
        Game game = gameService.initGame(roomId, roomMetadata);

        // 4. 게임 플레이어 정보 조회 후 연결
        List<GamePlayer> gamePlayers = gamePlayerService.getGamePlayersFromRoom(roomId);
        game.setGamePlayers(gamePlayers);

        // 5. 라운드 정보 초기화 후 연결
        List<Round> rounds = roundService.initRounds(game.getTotalRounds(), gamePlayers);
        game.setRounds(rounds);

        // 6. Redis에 저장 : GameMeta, GamePlayers, Rounds
        saveGame(game);

        //todo: 7. AI 서버 메시지 받아오기

        // 8. 시작 이벤트 브로드캐스트
        broadcastStartEvent(userUuid, roomId, game);

    }





    private void saveGame(Game game) {
        gameService.saveGameMeta(game);
        gamePlayerService.savePlayers(game.getRoomId(), game.getGamePlayers());
        roundService.saveRounds(game.getRoomId(), game.getRounds());
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
