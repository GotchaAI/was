package socket_server.domain.game.state;

import reactor.core.publisher.Mono;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.AIGameStartReq;
import socket_server.domain.game.dto.AISaysRes;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.service.GameFlowManager;
import socket_server.domain.game.util.WordUtils;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 게임 시작 전의 초기 상태를 나타내는 클래스.
 * 이 상태에서는 게임 시작 이벤트(GAME_START)만 처리합니다.
 */
public class PreGameState extends AbstractGameState {
    /**
     * 이벤트를 처리합니다.
     * GAME_START 이벤트가 발생하면 게임을 초기화하고 DrawingPhaseState로 전환합니다.
     * @param context 게임 컨텍스트
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 인자 (첫 번째 인자는 userUuid)
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    @Override
    public Mono<Void> handleEvent(GameContext context, GameEventType eventType, String... args) {
        if (eventType == GameEventType.GAME_START) {
            String userUuid = args[0];
            GameFlowManager manager = context.getGameFlowManager();

            // 1. 게임 시작 가능 여부 확인 및 방 메타데이터 조회
            RoomMetadata roomMetadata = manager.getRoomUserService().validateRoomOwnerAndGetRoomMetadata(context.getRoomId(), userUuid);
            manager.getRoomUserService().checkGameStart(context.getRoomId(), roomMetadata.getGameType());

            // 2. 기존 게임 메타데이터 존재 여부 확인 (중복 시작 방지)
            Map<Object, Object> gameMetaMap = manager.getGameRepository().findGameMeta(context.getRoomId());
            if(!gameMetaMap.isEmpty()) {
                return Mono.error(new SocketCustomException(ErrorType.GAME, GameExceptionCode.INVALID_GAME_STATUS));
            }

            // 3. 게임 메타데이터 생성
            Game game = Game.builder().
                    roomId(context.getRoomId()).
                    gameStatus(GameStatus.GAME_STARTED).
                    gameType(roomMetadata.getGameType()).
                    difficulty(roomMetadata.getDifficulty()).
                    currentRound(0).
                    totalRounds(roomMetadata.getRoundCount()).build();

            // 4. 게임 플레이어 정보 조회 및 연결
            List<GamePlayer> gamePlayers = manager.getRoomUserRepository().findUsersByRoomId(context.getRoomId(), ErrorType.GAME)
                    .stream().map(RoomUserInfo::toGamePlayer).toList();
            game.setGamePlayers(gamePlayers);

            // 5. 라운드 정보 초기화 및 연결
            List<Round> rounds = initRounds(game.getTotalRounds(), gamePlayers);
            game.setRounds(rounds);

            // 6. Redis에 게임 데이터 저장
            saveGame(context, game);

            // 7. AI 서버에 게임 시작 메시지 요청 및 응답 처리
            return manager.getAiClientService().getGameStartMessage(context.getRoomId(), new AIGameStartReq(gamePlayers.stream().map(GamePlayer::getNickname).toList()))
                .flatMap(aiSays -> {
                    // 8. 시작 이벤트 브로드캐스트
                    manager.getGameBroadCaster().broadcastStartEvent(userUuid, context.getRoomId(), new AISaysRes(game, aiSays));
                    // 9. 상태를 DrawingPhaseState로 전환
                    context.setState(new DrawingPhaseState());
                    // 10. 5초 후 ROUND_START 이벤트 트리거
                    return Mono.delay(Duration.ofSeconds(5)).then(context.handleEvent(GameEventType.ROUND_START));
                });
        }
        // 지원하지 않는 이벤트는 기본 구현을 따름
        return super.handleEvent(context, eventType, args);
    }

    /**
     * 이 상태의 GameStatus를 반환합니다.
     * 게임 시작 전이므로 null을 반환합니다.
     * @return null
     */
    @Override
    public GameStatus getStatus() {
        return null;
    }

    /**
     * 게임 데이터를 Redis에 저장합니다.
     * @param context 게임 컨텍스트
     * @param game 저장할 게임 객체
     */
    private void saveGame(GameContext context, Game game) {
        GameFlowManager manager = context.getGameFlowManager();
        manager.getGameRepository().saveGameMeta(GameMeta.fromGame(game));
        savePlayers(context, game.getRoomId(), game.getGamePlayers());
        manager.getRoundRepository().saveRoundMetasString(game.getRoomId(),
                manager.getJsonSerializer().serialize(game.getRounds().stream().map(Round::fromRound).toList(), ErrorType.GAME));

        for (Round round : game.getRounds()) {
            String wordsJson =
                    manager.getJsonSerializer().serialize(round.getWords().stream().map(Word::fromWord).toList(), ErrorType.GAME);
            manager.getRoundRepository().saveWordMetasString(game.getRoomId(), round.getRoundIndex(), wordsJson);
        }
    }

    /**
     * 게임 플레이어 데이터를 Redis에 저장합니다.
     * @param context 게임 컨텍스트
     * @param roomId 게임방 ID
     * @param gamePlayers 저장할 게임 플레이어 목록
     */
    private void savePlayers(GameContext context, String roomId, List<GamePlayer> gamePlayers) {
        GameFlowManager manager = context.getGameFlowManager();
        String gamePlayersJson = manager.getJsonSerializer().serialize(gamePlayers, ErrorType.GAME);
        manager.getGamePlayerRepository().savePlayersString(roomId, gamePlayersJson);
    }

    /**
     * 라운드 정보를 초기화하고 단어를 할당합니다.
     * @param totalRounds 전체 라운드 수
     * @param gamePlayers 게임 플레이어 목록
     * @return 초기화된 라운드 목록
     */
    private List<Round> initRounds(int totalRounds, List<GamePlayer> gamePlayers) {
        List<Round> rounds = new ArrayList<>();
        List<Integer> indexes = WordUtils.getRandomIndexes(totalRounds * 2);
        for(int i = 0; i < totalRounds; i++) {
            List<Word> words = new ArrayList<>();
            for(int j = 0; j < 2; j++){
                Word word = Word.builder()
                        .wordIndex(j)
                        .word(WordUtils.getKorWord(indexes.get(i * 2 + j)))
                        .drawerUuid(gamePlayers.get(j).getPlayerUuid())
                        .drawerName(gamePlayers.get(j).getNickname())
                        .aiGuesses(new ArrayList<>())
                        .playerGuesses(new ArrayList<>())
                        .AIPredictions(new ArrayList<>()).build();
                words.add(word);
            }

            Round round = Round.builder().
                    roundIndex(i).
                    currentWordIndex(0).
                    words(words).
                    build();
            rounds.add(round);
        }
        return rounds;
    }
}
