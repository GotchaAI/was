package Gotcha.domain.inquiry.dto;

import gotcha_domain.inquiry.Inquiry;

import java.time.LocalDateTime;

public record InquiryRes(
        Long inquiryId,
        String title,
        String writer,
        String content,
        Boolean isPrivate,
        LocalDateTime createdAt,
        Boolean isSolved,
        AnswerRes answer
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
