package Gotcha.domain.lulu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StartRes(
    @JsonProperty("game_id")
    String gameId
) {
}
