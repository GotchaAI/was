package socket_server.domain.game.dto;

import socket_server.domain.game.model.AIPrediction;

import java.util.List;


public record AIGuessImageRes (
        String filename,
        List<AIPrediction> result
){
}
