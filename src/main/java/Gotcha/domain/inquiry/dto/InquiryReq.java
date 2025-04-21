package Gotcha.domain.inquiry.dto;

import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record InquiryReq(
        @Schema(description = "QnA 제목", example = "게임이 너무 재밌는 것 같아요!")
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        String title,

        @Schema(description = "QnA 질문 내용", example = "이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 ...더보기")
        @NotBlank(message = "내용은 필수 입력 사항입니다.")
        String content,

        @Schema(description = "비공개 여부", example = "false", defaultValue = "false")
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
