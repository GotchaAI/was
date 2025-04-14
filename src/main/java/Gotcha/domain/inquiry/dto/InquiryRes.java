package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Answer;
import Gotcha.domain.inquiry.entity.Inquiry;

import java.time.LocalDateTime;

public record InquiryRes(
        Long qnaId,
        String title,
        String writer,
        String content,
        Boolean isPrivate,
        LocalDateTime createdAt,
        Boolean isSolved,
        AnswerRes answerRes
) {
    public static InquiryRes fromEntity(Inquiry inquiry){
        return new InquiryRes(
                inquiry.getId(),
                inquiry.getTitle(),
                inquiry.getWriter().getNickname(),
                inquiry.getContent(),
                inquiry.getIsSecret(),
                inquiry.getCreatedAt(),
                inquiry.getIsSolved(),
                AnswerRes.fromEntity(inquiry.getAnswer())
        );
    }
}
