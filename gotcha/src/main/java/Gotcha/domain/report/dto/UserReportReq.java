package Gotcha.domain.report.dto;


import gotcha_domain.report.UserReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import gotcha_domain.chat.ChatType;

import java.time.LocalDateTime;

public record UserReportReq (
        @Schema(description = "채팅 타입", example = "ALL")
        @NotNull(message = "채팅 타입은 필수 입니다.")
        ChatType chatType,

        @Schema(description = "식별자", example = "1234")
        String identifier,

        @Schema(description = "신고 유형", example = "OTHER")
        @NotNull(message = "신고 유형은 필수 입니다.")
        UserReportType reportType,

        @Schema(description = "신고 사유", example = "욕설 너무 많이 합니다.")
        @NotNull(message = "신고 사유는 필수 입니다.")
        String reason,

        @Schema(description = "신고 대상 채팅 시간", example = "2025-06-01T16:23:29.108754")
        @NotNull(message = "신고 대상 채팅 시간은 필수 입니다.")
        LocalDateTime chatTime,

        @Schema(description = "신고 대상 사용자 닉네임", example ="테스트")
        @NotNull(message = "신고 대상 사용자의 닉네임은 필수 입니다.")
        String reportedNickname
){
}
