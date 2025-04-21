package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Inquiry;

import java.time.LocalDateTime;

public record InquirySummaryRes(
        Long inquiryId,
        String title,
        String writer,
        Boolean isPrivate,
        LocalDateTime createdAt,
        Boolean isSolved
) {
    public static InquirySummaryRes fromEntity(Inquiry inquiry){
        return new InquirySummaryRes(
                inquiry.getId(),
                inquiry.getTitle(),
                inquiry.getWriter().getNickname(),
                inquiry.getIsSecret(),
                inquiry.getCreatedAt(),
                inquiry.getIsSolved()
        );
    }
}
