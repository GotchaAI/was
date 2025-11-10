package Gotcha.domain.sanction.dto;

import gotcha_domain.sanction.SanctionType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SanctionReq {
    private Long targetUserId;
    private Long adminUserId;
    private String reason;
    private SanctionType sanctionType;
    private Long durationDays; // 제재 기간 (일 단위), 영구 정지인 경우 null
}
