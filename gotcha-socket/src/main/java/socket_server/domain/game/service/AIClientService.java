package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import socket_server.domain.game.dto.AIGameStartReq;
import socket_server.domain.game.dto.AIGameStartRes;

@Service
@RequiredArgsConstructor
public class AIClientService {

    private final WebClient webClient;

    private static final String AI_SERVER_BASE_URL = "http://localhost:8000/api/v1/";


    public AIGameStartRes getGameStartMessage(String roomId, AIGameStartReq request){
        return webClient.post()
                .uri(AI_SERVER_BASE_URL + "chat/" + roomId + "/start")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AIGameStartRes.class)
                .block();
    }

}
