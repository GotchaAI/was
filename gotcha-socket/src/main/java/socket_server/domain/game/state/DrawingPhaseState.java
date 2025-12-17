package socket_server.domain.game.state;

import reactor.core.publisher.Mono;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.AIGuessImageReq;
import socket_server.domain.game.dto.AIGuessImageRes;
import socket_server.domain.game.dto.AIRoundStartReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.RoundMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.service.GameFlowManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 게임의 그리기 단계(Drawing Phase)를 나타내는 클래스.
 * 이 상태에서는 라운드 시작(ROUND_START) 및 그림 제출(DRAWING_SUBMIT) 이벤트를 처리합니다.
 */
public class DrawingPhaseState extends AbstractGameState {

    /**
     * 이벤트를 처리합니다.
     * ROUND_START 이벤트는 라운드를 시작하고, DRAWING_SUBMIT 이벤트는 그림 제출을 처리합니다.
     * @param context 게임 컨텍스트
     * @param eventType 처리할 이벤트 타입
     * @param args 이벤트 인자 (DRAWING_SUBMIT의 경우 drawerUuid, imageURL)
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    @Override
    public Mono<Void> handleEvent(GameContext context, GameEventType eventType, String... args) {
        return switch (eventType) {
            case ROUND_START -> handleRoundStart(context);
            case DRAWING_SUBMIT -> handleDrawingSubmit(context, args[0], args[1]);
            default -> super.handleEvent(context, eventType, args);
        };
    }

    /**
     * 이 상태의 GameStatus를 반환합니다.
     * @return GameStatus.DRAWING_PHASE
     */
    @Override
    public GameStatus getStatus() {
        return GameStatus.DRAWING_PHASE;
    }

    /**
     * 라운드 시작 이벤트를 처리합니다.
     * @param context 게임 컨텍스트
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleRoundStart(GameContext context) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = getGameMetaByRoomId(context.getRoomId(), manager);

        // 게임 종료 여부 확인
        if (isGameEnded(gameMeta)) {
            return Mono.error(new SocketCustomException(ErrorType.GAME, GameExceptionCode.ALREADY_FINISHED_GAME));
        }

        int currentRound = gameMeta.getCurrentRound();

        // 라운드 메타데이터 로드 및 업데이트
        String roundMetasJson = manager.getRoundRepository().findRoundMetasString(context.getRoomId());
        List<RoundMeta> roundMetaList = manager.getJsonSerializer().deserializeList(roundMetasJson, RoundMeta.class, ErrorType.GAME);

        gameMeta.setCurrentRound(currentRound);
        gameMeta.setGameStatus(GameStatus.DRAWING_PHASE);
        manager.getGameRepository().saveGameMeta(gameMeta);

        RoundMeta currentRoundMeta = roundMetaList.get(currentRound);

        manager.getRoundRepository().saveRoundMetasString(context.getRoomId(), manager.getJsonSerializer().serialize(roundMetaList, ErrorType.GAME));

        // AI 서버에 라운드 시작 메시지 요청 및 응답 처리
        return manager.getAiClientService().getRoundStartMessage(
                context.getRoomId(),
                new AIRoundStartReq(currentRound, gameMeta.getTotalRounds())
        ).doOnSuccess(aiSays -> {
            // 드로잉 종료 시간 설정 및 이벤트 브로드캐스트
            currentRoundMeta.setDrawingEndTime(LocalDateTime.now().plusSeconds(30));
            manager.getGameBroadCaster().broadcastGameEvent("SYSTEM", context.getRoomId(), GameEventType.ROUND_START, currentRoundMeta, aiSays);
        }).then();
    }

    /**
     * 그림 제출 이벤트를 처리합니다.
     * @param context 게임 컨텍스트
     * @param drawerUuid 그림을 그린 유저의 UUID
     * @param imageURL 제출된 그림의 URL
     * @return 비동기 작업 완료를 나타내는 Mono<Void> 객체
     */
    private Mono<Void> handleDrawingSubmit(GameContext context, String drawerUuid, String imageURL) {
        GameFlowManager manager = context.getGameFlowManager();
        GameMeta gameMeta = validateDrawingStatusAndGetGameMeta(context.getRoomId(), manager);

        int currentRound = gameMeta.getCurrentRound();

        List<WordMeta> wordMetas = getWordMetas(context.getRoomId(), currentRound, manager);
        WordMeta targetWord = wordMetas.get(getWordIndexByDrawerUuid(wordMetas, drawerUuid));

        // 이미 제출된 그림인지 확인
        if (targetWord.isSubmitted()) {
            return Mono.error(new SocketCustomException(ErrorType.GAME, GameExceptionCode.DRAWING_ALREADY_SUBMITTED));
        }

        // 그림 정보 업데이트
        targetWord.setImageURL(imageURL);
        targetWord.setSubmitted(true);

        // 업데이트된 단어 메타데이터 저장
        String wordsJson = manager.getJsonSerializer().serialize(wordMetas, ErrorType.GAME);
        manager.getRoundRepository().saveWordMetasString(context.getRoomId(), currentRound, wordsJson);

        // AI 서버에 이미지 추측 요청 및 응답 처리
        return manager.getAiClientService().getGuessImage(new AIGuessImageReq(imageURL))
                .map(AIGuessImageRes::result)
                .flatMap(predictions -> {
                    String predictionsJson = manager.getJsonSerializer().serialize(predictions, ErrorType.GAME);
                    manager.getRoundRepository().saveAIPredictionsString(context.getRoomId(), currentRound, getWordIndexByDrawerUuid(wordMetas, drawerUuid), predictionsJson);

                    // 모든 그림이 제출되었는지 확인 후 다음 단계로 전환
                    if (checkAllDrawingSubmitted(context.getRoomId(), currentRound, manager)) {
                        context.setCurrentState(new GuessingState());
                        return context.handleEvent(GameEventType.GUESS_START); // 추측 단계 시작 이벤트 트리거
                    }
                    return Mono.empty();
                }).then();
    }

    /**
     * 그림 제출 상태 유효성 검사 및 게임 메타데이터 조회
     * @param roomId 게임방 ID
     * @param manager GameFlowManager 인스턴스
     * @return 유효성 검사 후 GameMeta 객체
     * @throws SocketCustomException 게임 상태가 유효하지 않을 경우 발생
     */
    private GameMeta validateDrawingStatusAndGetGameMeta(String roomId, GameFlowManager manager){
        GameMeta gameMeta = getGameMetaByRoomId(roomId, manager);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.DRAWING_SUBMIT)){
            throw new SocketCustomException(ErrorType.GAME, GameExceptionCode.INVALID_GAME_STATUS);
        }
        return gameMeta;
    }

    /**
     * 라운드의 단어 메타데이터를 조회합니다.
     * @param roomId 게임방 ID
     * @param roundIndex 라운드 인덱스
     * @param manager GameFlowManager 인스턴스
     * @return 단어 메타데이터 목록
     */
    private List<WordMeta> getWordMetas(String roomId, int roundIndex, GameFlowManager manager) {
        String wordMetasJson = manager.getRoundRepository().findWordMetasString(roomId, roundIndex);
        if(wordMetasJson == null) return new ArrayList<>();
        return manager.getJsonSerializer().deserializeList(wordMetasJson, WordMeta.class, ErrorType.GAME);
    }

    /**
     * 그림을 그린 유저의 UUID를 통해 단어 인덱스를 조회합니다.
     * @param wordMetas 단어 메타데이터 목록
     * @param drawerUuid 그림을 그린 유저의 UUID
     * @return 단어 인덱스
     * @throws SocketCustomException 유효하지 않은 drawerUuid일 경우 발생
     */
    private int getWordIndexByDrawerUuid(List<WordMeta> wordMetas, String drawerUuid) {
        return wordMetas.stream()
                .filter(word -> word.getDrawerUuid().equals(drawerUuid))
                .findFirst()
                .orElseThrow(() -> new SocketCustomException(ErrorType.GAME, GameExceptionCode.INVALID_DRAWER_ID))
                .getWordIndex();
    }

    /**
     * 모든 그림이 제출되었는지 확인합니다.
     * @param roomId 게임방 ID
     * @param currentRound 현재 라운드 인덱스
     * @param manager GameFlowManager 인스턴스
     * @return 모든 그림이 제출되었으면 true, 아니면 false
     */
    private boolean checkAllDrawingSubmitted(String roomId, int currentRound, GameFlowManager manager){
        List<WordMeta> wordMetas = getWordMetas(roomId, currentRound, manager);
        return wordMetas.stream().allMatch(WordMeta::isSubmitted);
    }

    /**
     * 게임이 종료되었는지 확인합니다.
     * @param gameMeta 게임 메타데이터
     * @return 게임이 종료되었으면 true, 아니면 false
     */
    public boolean isGameEnded(GameMeta gameMeta){
        return gameMeta.getCurrentRound() >= gameMeta.getTotalRounds();
    }
}
