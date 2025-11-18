package Gotcha.domain.sanction.dto;

import java.time.LocalDate;

public record SanctionUserListRes(
        String nickname,
        LocalDate createDate,
        String email,
        int reportedCount,
        int warningCount
) {
}
