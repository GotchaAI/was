package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import socket_server.domain.game.dto.AIGameStartReq;
import socket_server.domain.game.dto.AIGuessStartReq;
import socket_server.domain.game.dto.AIRoundStartReq;

@Service
@RequiredArgsConstructor
public class AIClientService {

    private final WebClient webClient;

    @Value("${ai-server.url}")
    private String AI_SERVER_BASE_URL;

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

}
