package Gotcha.domain.lulu.service;

import Gotcha.domain.lulu.dto.StartRes;
import Gotcha.domain.lulu.dto.TaskEvalReq;
import Gotcha.domain.lulu.dto.TaskEvalRes;
import Gotcha.domain.lulu.dto.TaskStartRes;
import Gotcha.domain.lulu.exception.LuLuExceptionCode;
import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LuLuAIClientService {

    private final WebClient webClient;

    @Value("${ai-server.url}")
    private String AI_SERVER_BASE_URL;


    /**
     * 루루 게임 시작. 게임ID 발급받음
     */
    public StartRes startGame(){
        try {
            StartRes res = webClient.get()
                    .uri(AI_SERVER_BASE_URL + "lulu/start")
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(
                                    new CustomException(LuLuExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(StartRes.class)
                    .block();
            if (res == null || res.gameId() == null)
                throw new CustomException(LuLuExceptionCode.AI_SERVER_FORMAT_ERROR);
            return res;
        } catch (WebClientResponseException e) {
            log.error("LuLuAIClientService startGame error", e);
            throw new CustomException(LuLuExceptionCode.AI_SERVER_FORMAT_ERROR);
        }
    }


    /**
     * 루루 키워드 제시
     */
    public TaskStartRes generateTask(String gameId){
        try {
            TaskStartRes res = webClient.get()
                    .uri(AI_SERVER_BASE_URL + "lulu/task/" + gameId)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(
                            )
                            , clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(
                                    new CustomException(LuLuExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(TaskStartRes.class)
                    .block();
            if (res == null || res.keyword() == null || res.situation() == null){
                log.error(String.valueOf(res));
                throw new CustomException(LuLuExceptionCode.AI_SERVER_FORMAT_ERROR);
            }
            return res;
        } catch (WebClientResponseException e) {
            log.error("LuLuAIClientService generateTask error", e);
            throw new CustomException(LuLuExceptionCode.AI_SERVER_FORMAT_ERROR);
        }
    }


    /**
     * 루루 키워드 평가
     */
    public TaskEvalRes evaluateTask(String gameId, TaskEvalReq taskEvalReq){
        String caption = getImageCaption(taskEvalReq.imageURL());
        return requestEvaluate(gameId, caption);
    }

    private TaskEvalRes requestEvaluate(String gameId, String imageCaption){
        Map<String, String> body = Map.of("description", imageCaption);
        try {
            TaskEvalRes res = webClient.post()
                    .uri(AI_SERVER_BASE_URL + "lulu/task/" + gameId)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(
                            )
                            , clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(
                                    new CustomException(LuLuExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(TaskEvalRes.class)
                    .block();
            if (res == null || res.score() == null || res.feedback() == null || res.gameId() == null)
                throw new CustomException(LuLuExceptionCode.AI_SERVER_FORMAT_ERROR);
            return res;
        } catch (WebClientResponseException e) {
            log.error("LuLuAIClientService evaluateTask error", e);
            throw new CustomException(LuLuExceptionCode.AI_SERVER_FORMAT_ERROR);
        }
    }





    /**
     * 이미지 캡셔닝
     */
    public String getImageCaption(String imageUrl){
        Map<String, String> body = Map.of("imageURL", imageUrl);
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "image/caption")
                .bodyValue(body)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(
                        )
                        , clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(
                                new CustomException(LuLuExceptionCode.AI_SERVER_ERROR))))
                .bodyToMono(String.class)
                .block();
    }


}
