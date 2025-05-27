package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.domain.game.dto.AIGuessImageReq;
import socket_server.domain.game.enumType.GameEventType;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.AiPrediction;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrawingSubmitService {

    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GuessFlowService guessFlowService;
    private final AIClientService aiClientService;


    public void submitDrawing(String roomId, String drawerUuid, String imageURL) {
        // 0. 게임 메타정보 조회
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        if(!gameMeta.getGameStatus().canHandleEvent(GameEventType.DRAWING_SUBMIT)){
            throw new CustomException(GameExceptionCode.INVALID_GAME_STATUS);
        }

        // 1. current round 가져오기
        int currentRound = getCurrentRoundIndex(roomId);

        // 2. word 찾고 업데이트
        List<WordMeta> wordMetas = findAndUpdateWordMeta(roomId, currentRound, drawerUuid, imageURL);

        // 3. 저장
        roundRepository.saveWordMetas(roomId, currentRound, wordMetas);



        // 4. AI PREDICTION 받아서 SAVE !!!!
        List<AiPrediction> predictions = aiClientService.getGuessImage(new AIGuessImageReq(imageURL)).result();
        roundRepository.saveAIPredictions(roomId, currentRound, getWordIndexByDrawerUuid(wordMetas, drawerUuid), predictions);


        if(checkAllDrawingSubmitted(roomId)) {
            guessFlowService.startGuessing(roomId);
        }
    }

    private int getCurrentRoundIndex(String roomId){
        GameMeta gameMeta = gameRepository.findGameMeta(roomId);
        return gameMeta.getCurrentRound();
    }


    private int getWordIndexByDrawerUuid(List<WordMeta> wordMetas, String drawerUuid) {
        return wordMetas.stream()
                .filter(word -> word.getDrawerUuid().equals(drawerUuid))
                .findFirst()
                .orElseThrow(() -> new CustomException(GameExceptionCode.INVALID_DRAWER_ID))
                .getWordIndex();
    }


    private List<WordMeta> findAndUpdateWordMeta(String roomId, int roundIndex, String drawerUuid, String imageURL) {
        List<WordMeta> wordMetas = roundRepository.findWordMetas(roomId, roundIndex);

        WordMeta targetWord = wordMetas.get(getWordIndexByDrawerUuid(wordMetas, drawerUuid));

        if (targetWord.isSubmitted()) {
            throw new CustomException(GameExceptionCode.DRAWING_ALREADY_SUBMITTED);
        }

        targetWord.setImageURL(imageURL);
        targetWord.setSubmitted(true);

        return wordMetas;
    }


    private boolean checkAllDrawingSubmitted(String roomId){
        int currentRound = getCurrentRoundIndex(roomId);
        List<WordMeta> wordMetas = roundRepository.findWordMetas(roomId, currentRound);
        return wordMetas.stream().allMatch(WordMeta::isSubmitted);
    }

}
