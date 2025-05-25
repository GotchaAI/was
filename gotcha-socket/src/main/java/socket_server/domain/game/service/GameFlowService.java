package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import socket_server.common.config.RedisMessage;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.GameEventType;
import socket_server.domain.game.dto.GameRes;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.*;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;
import socket_server.domain.room.dto.EventRes;
import socket_server.domain.room.dto.EventType;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.service.RoomService;

import java.time.LocalDateTime;
import java.util.List;

import static socket_server.common.constants.WebSocketConstants.GAME_PREFIX;
import static socket_server.common.constants.WebSocketConstants.ROOM_EVENT;
/**
 * 게임 전체 흐름 담당.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class GameFlowService {


    private final GamePlayerService gamePlayerService;
    private final RoomService roomService;
    private final RoundService roundService;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final JsonSerializer jsonSerializer;
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public void startGame(String roomId, String userUuid)  {
        // 1. 게임 시작 가능한지(레디 상태, 플레이어 수) check 후 방 메타정보 조회
        RoomMetadata roomMetadata = roomService.getHostingRoomMetadata(roomId, userUuid);
        roomService.checkGameStart(roomId, roomMetadata.getGameType());

        // 2. 게임 메타데이터 생성
        Game game = Game.builder().
                roomId(roomId).
                gameType(roomMetadata.getGameType()).
                difficulty(roomMetadata.getDifficulty()).
                currentRound(0).
                totalRounds(roomMetadata.getRoundCount()).build();

        // 3. 게임 플레이어 정보 조회 후 연결
        List<GamePlayer> gamePlayers = gamePlayerService.getGamePlayersFromRoom(roomId);
        game.setGamePlayers(gamePlayers);

        // 4. 라운드 정보 초기화 후 연결
        List<Round> rounds = roundService.initRounds(game.getTotalRounds(), gamePlayers);
        game.setRounds(rounds);

        // 5. Redis에 저장 : GameMeta, GamePlayers, Rounds
        saveGame(game);

        //todo: 6. AI 서버 메시지 받아오기

        // 7. 시작 이벤트 브로드캐스트
        broadcastStartEvent(userUuid, roomId, game);

        // 8. 5초 후 게임 시작(EntryPoint)
        try{
            Thread.sleep(5000); // 5000ms = 5초
        } catch (InterruptedException e){  }
        startNextRound(userUuid, roomId);

    }

    /**
     * 1. 게임 메타 정보 조회 후 currentRound 조회
     * 2. currentRound + 1 한 다음 게임 메타정보 저장
     * 3. roundIndex = currentRound + 1, 해당 라운드 정보 조회
     * 4. drawingEndTime 설정 후 데이터 broadcast
     * 5. 라운드 메타 정보 저장
     */
    public void startNextRound(String userUuid, String roomId){
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);

        if(!canStartNextRound(gameMeta))
            throw new CustomException(GameExceptionCode.ALREADY_FINISHED_GAME);

        int currentRound = getNextRoundIndex(gameMeta);
        gameMeta.setCurrentRound(currentRound);
        gameRepository.saveGameMeta(gameMeta);

        List<RoundMeta> roundMetaList = roundRepository.findRoundMetas(roomId);
        RoundMeta currentRoundMeta = roundMetaList.get(currentRound);

        currentRoundMeta.setDrawingEndTime(LocalDateTime.now().plusSeconds(30));
        broadcastRoundMeta(userUuid, roomId,  currentRoundMeta);
    }




    // 게임 종료 check시 반드시 필요
    public boolean canStartNextRound(GameMeta gameMeta){
        return gameMeta.getCurrentRound() <= gameMeta.getTotalRounds();
    }

    private int getNextRoundIndex(GameMeta gameMeta) {
        return gameMeta.getCurrentRound() + 1;
    }



    /**
     * 게임 전체 정보 조회
     */
    public Game getGame(String roomId) {
        Game game = Game.fromGameMeta(gameRepository.findGameMeta(roomId));

        // Round 가져와서 roundIndex로 WordMeta 조회
        List<Round> rounds = roundRepository.findRoundMetas(roomId).stream().map(RoundMeta::toRound).toList();
        for(Round round: rounds) {
            // Word 가져와서 wordIndex로 Guess 조회
            List<Word> words = roundRepository.findWords(roomId, round.getRoundIndex()).stream().map(WordMeta::toWord).toList();
            for(Word word: words) {
                List<Guess> guesses = roundRepository.findGuesses(roomId, round.getRoundIndex(), word.getWordIndex());
                word.setGuesses(guesses);
            }
            round.setWords(words);
        }
        game.setRounds(rounds);
        return game;
    }



    private void broadcastRoundMeta(String userUuid, String roomId, RoundMeta roundMeta) {
        broadcastGameEvent(userUuid, roomId, GameEventType.ROUND_START, roundMeta);
    }

    private void saveGame(Game game) {
        gameRepository.saveGameMeta(GameMeta.fromGame(game));
        gamePlayerRepository.savePlayers(game.getRoomId(), game.getGamePlayers());
        roundRepository.saveRoundMetas(game.getRoomId(), game.getRounds());
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

    private void broadcastGameEvent(String senderUuid, String roomId, GameEventType gameEventType, Object data) {
        GameRes gameRes = new GameRes(
                gameEventType,
                data,
                LocalDateTime.now());

        objectRedisTemplate.convertAndSend(
                GAME_PREFIX + roomId,
                new RedisMessage(
                        senderUuid,
                        GAME_PREFIX + roomId,
                        jsonSerializer.serialize(gameRes)
                )
        );

        log.debug("Broadcasted {} game event in room {} from user {}", gameEventType, roomId, senderUuid);
    }
}
