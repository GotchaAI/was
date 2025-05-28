package socket_server.domain.game.service;

import gotcha_common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import socket_server.domain.game.dto.*;
import socket_server.common.exception.game.GameExceptionCode;

@Service
@RequiredArgsConstructor
public class AIClientService {

    private final WebClient webClient;

//    @Value("${ai-server.url}")
    private String AI_SERVER_BASE_URL = "http://localhost:8000/api/v1/";

    public String getGameStartMessage(String roomId, AIGameStartReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/start")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getRoundStartMessage(String roomId, AIRoundStartReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/round/start")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getGuessStartMessage(String roomId, AIGuessStartReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/guess/start")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public AIGuessImageRes getGuessImage(AIGuessImageReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "image/classify")
                .bodyValue(request)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                        new CustomException(GameExceptionCode.AI_SERVER_ERROR))))
                .bodyToMono(AIGuessImageRes.class)
                .block();

    }

    public String getGuessMessage(String roomId, AIGuessMessageReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/guess")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getGuessReactMessage(String roomId, AIGuessReactReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/guess/react")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getRoundEndMessage(String roomId, AIRoundEndReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/round/end")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getGameEndMessage(String roomId, AIGameEndReq request) {
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/end")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
