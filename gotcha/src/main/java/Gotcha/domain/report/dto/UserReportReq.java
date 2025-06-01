package Gotcha.domain.report.dto;


import gotcha_domain.report.UserReportType;
import jakarta.validation.constraints.NotNull;
import gotcha_domain.chat.ChatType;

import java.time.LocalDateTime;

public record UserReportReq (
        @NotNull
        ChatType chatType,

        @NotNull
        String identifier,

        @NotNull
        UserReportType reportType,

        @NotNull
        String reason,

        @NotNull
        LocalDateTime chatTime,

        @NotNull
        String reportedUuId
){
}
