package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Answer;

import java.time.LocalDateTime;

public record AnswerRes(
        String writer,
        String content,
        LocalDateTime createdAt
) {
    public static AnswerRes fromEntity(Answer answer){
        if (answer == null) return null;
        return new AnswerRes(
                answer.getWriter().getNickname(),
                answer.getContent(),
                answer.getCreatedAt()
        );
    }
}
