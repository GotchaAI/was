package socket_server.domain.game.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.domain.game.dto.*;
import socket_server.common.exception.game.GameExceptionCode;

import java.util.concurrent.TimeoutException;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIClientService {
    private final WebClient webClient;
    private final ErrorType GAME_ERROR = ErrorType.GAME;

    @Value("${ai-server.url}")
    private String AI_SERVER_BASE_URL;

    public String getGameStartMessage(String roomId, AIGameStartReq request){
       try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/start")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                    , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e){
            log.error("[AI Server Exception] error on GAME_START: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public String getRoundStartMessage(String roomId, AIRoundStartReq request){
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/round/start")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        }catch (Exception e){
            log.error("[AI Server Exception] error on ROUND_START: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public String getGuessStartMessage(String roomId, AIGuessStartReq request){
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/guess/start")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e){
            log.error("[AI Server Exception] error on GUESS_START: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public AIGuessImageRes getGuessImage(AIGuessImageReq request){
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "image/classify")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(AIGuessImageRes.class)
                    .block();
        } catch (Exception e) {
            log.error("[AI Server Exception] error on GUESS IMAGE: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public String getGuessMessage(String roomId, AIGuessMessageReq request){
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/guess")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e) {
            log.error("[AI Server Exception] error on GUESS MESSAGE: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public String getGuessReactMessage(String roomId, AIGuessReactReq request){
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/guess/react")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e) {
            log.error("[AI Server Exception] error on GUESS REACT: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public String getRoundEndMessage(String roomId, AIRoundEndReq request){
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/round/end")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e) {
            log.error("[AI Server Exception] error on ROUND END: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }
    }

    public String getGameEndMessage(String roomId, AIGameEndReq request) {
        try {
            return webClient.post()
                    .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/end")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError()
                            , clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
        } catch (Exception e) {
            log.error("[AI Server Exception] error on GAME END: {}", e.getClass().getName());
            throw new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
        }

    }
}
