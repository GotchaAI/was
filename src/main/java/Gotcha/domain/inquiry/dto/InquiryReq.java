package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;

public record InquiryReq(
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        String title,
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        String content,
        Boolean isPrivate
) {
    public InquiryReq {
        if (isPrivate == null) {
            isPrivate = false;  // default value
        }
    }
    public Inquiry toEntity(User writer){
        return Inquiry.builder().
                title(title).
                content(content).
                writer(writer).
                isSecret(isPrivate).
                build();
    }
}
