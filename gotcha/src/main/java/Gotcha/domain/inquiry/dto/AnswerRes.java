package Gotcha.domain.inquiry.dto;

import gotcha_domain.inquiry.Answer;

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
