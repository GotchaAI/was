package socket_server.domain.game.model;

import lombok.Getter;

@Getter
public class AiPrediction {
    private String predicted;
    private Double confidence;
}
