package Gotcha.domain.sanction.dto;

import gotcha_domain.sanction.SanctionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SanctionReq {

    @NotBlank(message = "제재 대상의 ID는 필수입니다.")
    private String targetUserId;

    // adminUserId 필드는 보안을 위해 제거하고, Controller에서 직접 인증 정보를 사용합니다.

    @NotBlank(message = "제재 사유는 필수입니다.")
    private String reason;

    @NotNull(message = "제재 유형은 필수입니다.")
    private SanctionType sanctionType;

    private Long sourceReportId; // 제재의 근거가 되는 신고 ID (nullable)

    private Long durationDays; // 제재 기간 (일 단위), TEMP_BAN일 때 필요
}
