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

    /**
     * AI 서버로 POST 요청을 보내는 제네릭 메서드.
     * 비동기 호출을 처리하며, 응답을 Mono<R> 형태로 반환합니다.
     * @param path AI 서버의 API 경로
     * @param requestBody 요청 본문
     * @param responseClass 응답 본문을 매핑할 클래스 타입
     * @param errorContext 로깅 및 예외 처리를 위한 컨텍스트 문자열
     * @param <T> 요청 본문의 타입
     * @param <R> 응답 본문의 타입
     * @return AI 서버로부터의 응답을 담은 Mono 객체
     */
    private <T, R> Mono<R> postToAiServer(String path, T requestBody, Class<R> responseClass, String errorContext) {
        // todo: 비동기? 동기? 비동기? 동기? 비동기? 동기?
        // todo: 모노? 안모노? 모노? 안모노? 모노? 안모노?
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + path)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(AIErrorRes.class).flatMap(error -> {
                            log.error("AI Server Error on Path: \"{}\", StatusCode: {}, Content: {}", path, clientResponse.statusCode(), error.detail());
                            return Mono.error(
                                    new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR));
                        }))
                .bodyToMono(responseClass)
                .timeout(Duration.ofSeconds(60))
                .doOnError(e -> log.error("[AI Server Exception] error on {}: {}", errorContext, e.getClass().getName()))
                .onErrorMap(e -> {
                    if (e instanceof SocketCustomException) {
                        return e;
                    }
                    return new SocketCustomException(GAME_ERROR, GameExceptionCode.AI_SERVER_ERROR);
                });
    }

    /**
     * 게임 시작 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 게임 시작 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public String getGameStartMessage(String roomId, AIGameStartReq request) {
        // todo: 비동기? 동기? 비동기? 동기? 비동기? 동기?
        // todo: 모노? 안모노? 모노? 안모노? 모노? 안모노?
        return postToAiServer("myomyo/" + roomId + "/start", request, String.class, "GAME_START").block();
    }


    /**
     * 라운드 시작 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 라운드 시작 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public String getRoundStartMessage(String roomId, AIRoundStartReq request) {
        return postToAiServer("myomyo/" + roomId + "/round/start", request, String.class, "ROUND_START").block();
    }

    /**
     * 추측 시작 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 추측 시작 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public String getGuessStartMessage(String roomId, AIGuessStartReq request) {
        return postToAiServer("myomyo/" + roomId + "/guess/start", request, String.class, "GUESS_START").block();
    }

    /**
     * 이미지 추측 결과를 AI 서버에 요청합니다.
     * @param request 이미지 추측 요청 DTO
     * @return AI 서버로부터의 이미지 추측 결과를 담은 Mono<AIGuessImageRes> 객체
     */
    public AIGuessImageRes getGuessImage(AIGuessImageReq request) {
        return postToAiServer("classify", request, AIGuessImageRes.class, "GUESS_IMAGE").block();
    }



    /**
     * 추측 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 추측 메시지 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public String getGuessMessage(String roomId, AIGuessMessageReq request) {
        return postToAiServer("myomyo/" + roomId + "/guess", request, String.class, "GUESS_MESSAGE").block();
    }

    /**
     * 추측 반응 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 추측 반응 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public String getGuessReactMessage(String roomId, AIGuessReactReq request) {
        return postToAiServer("myomyo/" + roomId + "/guess/react", request, String.class, "GUESS_REACT").block();
    }

    /**
     * 라운드 종료 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 라운드 종료 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public Mono<String> getRoundEndMessage(String roomId, AIRoundEndReq request) {
        return postToAiServer("myomyo/" + roomId + "/round/end", request, String.class, "ROUND_END");
    }

    /**
     * 게임 종료 메시지를 AI 서버에 요청합니다.
     * @param roomId 게임방 ID
     * @param request 게임 종료 요청 DTO
     * @return AI 서버로부터의 응답 메시지를 담은 Mono<String> 객체
     */
    public String getGameEndMessage(String roomId, AIGameEndReq request) {
        return postToAiServer("myomyo/" + roomId + "/end", request, String.class, "GAME_END").block();
    }
}
