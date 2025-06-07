package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.AIGuessImageReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.AiPrediction;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawingSubmitService {
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GuessFlowService guessFlowService;
    private final AIClientService aiClientService;
    private final ErrorType GAME_ERROR = ErrorType.GAME;
    private final JsonSerializer jsonSerializer;

    public void submitDrawing(String roomId, String drawerUuid, String imageURL) {
        // 0. 게임 메타정보 조회
        GameMeta gameMeta = validateDrawingStatusAndGetGameMeta(roomId);

        // 1. current round 가져오기
        int currentRound = gameMeta.getCurrentRound();

        // 2. word 찾고 업데이트
        List<WordMeta> wordMetas = getWordMetas(roomId, currentRound); // NPE
        WordMeta targetWord = wordMetas.get(getWordIndexByDrawerUuid(wordMetas, drawerUuid));

        if (targetWord.isSubmitted()) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.DRAWING_ALREADY_SUBMITTED);
        }

        targetWord.setImageURL(imageURL);
        targetWord.setSubmitted(true);

        // 3. 저장
        String wordsJson = jsonSerializer.serialize(wordMetas, GAME_ERROR);
        roundRepository.saveWordMetasString(roomId, currentRound, wordsJson);

        // 4. AI PREDICTION 받아서 SAVE !!!!
        List<AiPrediction> predictions = aiClientService.getGuessImage(new AIGuessImageReq(imageURL)).result();
        String predictionsJson = jsonSerializer.serialize(predictions, GAME_ERROR);
        roundRepository.saveAIPredictionsString(roomId, currentRound, getWordIndexByDrawerUuid(wordMetas, drawerUuid), predictionsJson);


        if(checkAllDrawingSubmitted(roomId, currentRound)) {
            guessFlowService.startGuessingPhase(roomId);
        }
    }

    /**
     * 현재 GameEvent가 실행될 수 있는지를 확인 후 GameMeta 데이터 반환.
     */
    private GameMeta validateDrawingStatusAndGetGameMeta(String roomId){
        Map<Object, Object> gameDataMap = gameRepository.findGameMeta(roomId);
        if(gameDataMap.isEmpty()) {
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_ID);
        }
        GameMeta gameMeta = GameMeta.fromRedisMap(roomId, gameDataMap);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.DRAWING_SUBMIT)){
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_GAME_STATUS);
        }
        return gameMeta;
    }



    private int getWordIndexByDrawerUuid(List<WordMeta> wordMetas, String drawerUuid) {
//        log.info("wordMetas {}", wordMetas.toString());
        return wordMetas.stream() // NPE
                .filter(word -> word.getDrawerUuid().equals(drawerUuid))
                .findFirst()
                .orElseThrow(() -> new SocketCustomException(GAME_ERROR, GameExceptionCode.INVALID_DRAWER_ID))
                .getWordIndex();
    }


    private List<WordMeta> getWordMetas(String roomId, int roundIndex) {
        String wordMetasJson = roundRepository.findWordMetasString(roomId, roundIndex);
        if(wordMetasJson == null) return new ArrayList<>();
        return jsonSerializer.deserializeList(wordMetasJson, WordMeta.class, GAME_ERROR);
    }


    private boolean checkAllDrawingSubmitted(String roomId, int currentRound){
        List<WordMeta> wordMetas = getWordMetas(roomId, currentRound);
        return wordMetas.stream().allMatch(WordMeta::isSubmitted);
    }



}
