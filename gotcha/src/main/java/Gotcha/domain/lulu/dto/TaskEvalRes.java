package Gotcha.domain.lulu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskEvalRes(
        Integer score,
        String feedback,
        @JsonProperty("game_id")
        String gameId
) {
}

