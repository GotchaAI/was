package Gotcha.domain.lulu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskStartRes(
        @JsonProperty("game_id")
        String gameId,
        String keyword,
        String situation
) {
}
