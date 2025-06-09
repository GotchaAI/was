package Gotcha.domain.lulu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskEvalRes(
        Integer score,
        String feedback,
        Task task,
        @JsonProperty("game_id")
        String gameId
) {
}

class Task{
    @JsonProperty("hidden_keyword")
    String keyword;
    @JsonProperty("poestic_description")
    String description;
    @JsonProperty("game_id")
    String gameId;
}