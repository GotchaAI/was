package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Answer;
import jakarta.validation.constraints.NotBlank;

public record AnswerReq(
        @NotBlank(message = "답변 내용은 필수 입력 사항입니다.")
        String content
) {
        public Answer toEntity(){
                return Answer.builder().
                        content(content)
                        .build();
        }
}
