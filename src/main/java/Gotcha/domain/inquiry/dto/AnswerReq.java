package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Answer;
import Gotcha.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AnswerReq(
        @Schema(description = "답변 내용", example = "감사합니묘! 더 발전하는 갓챠가 되겠습니묘!")
        @NotBlank(message = "답변 내용은 필수 입력 사항입니다.")
        String content
) {
        public Answer toEntity(User writer){
                return Answer.builder().
                        content(content)
                        .writer(writer)
                        .build();
        }
}
