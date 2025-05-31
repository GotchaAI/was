package socket_server.domain.game.dto;

import socket_server.domain.game.model.AiPrediction;

import java.util.List;


public record AIGuessImageRes (
        String filename,
        List<AiPrediction> result
){
}
