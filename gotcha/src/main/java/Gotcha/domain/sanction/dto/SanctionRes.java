package Gotcha.domain.sanction.dto;

import gotcha_domain.sanction.SanctionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SanctionRes {
    private Long id;
    private String targetUserName;
    private String adminUserName;
    private SanctionType sanctionType;
    private String reason;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static SanctionRes fromEntity(gotcha_domain.sanction.UserSanction sanction) {
        return SanctionRes.builder()
                .id(sanction.getId())
                .targetUserName(sanction.getUser().getNickname())
                .adminUserName(sanction.getAdmin().getNickname())
                .sanctionType(sanction.getSanctionType())
                .reason(sanction.getReason())
                .expiresAt(sanction.getExpiresAt())
                .createdAt(sanction.getCreatedAt())
                .build();
    }

}
