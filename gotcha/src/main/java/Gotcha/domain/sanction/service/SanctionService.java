package Gotcha.domain.sanction.service;

import Gotcha.domain.report.service.UserReportService;
import Gotcha.domain.sanction.dto.SanctionReq;
import Gotcha.domain.sanction.dto.SanctionRes;
import Gotcha.domain.sanction.repository.SanctionRepository;
import gotcha_domain.report.UserReport;
import gotcha_domain.sanction.SanctionType;
import gotcha_domain.sanction.UserSanction;
import gotcha_domain.user.User;
import gotcha_user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanctionService {

    private final SanctionRepository sanctionRepository;
    private final UserService userService;
    private final UserReportService userReportService;

    @Transactional
    public SanctionRes sanctionUser(SanctionReq sanctionReq, String adminId) {
        // admin User 조회
        User adminUser = userService.findUserByUuid(adminId);

        //1. target User 조회
        User targetUser = userService.findUserByUuid(sanctionReq.getTargetUserId());

        // 2. source Report 조회 (존재한다면)
        UserReport sourceReport = null;
        if(sanctionReq.getSourceReportId() != null){
            sourceReport = userReportService.findUserReportById(sanctionReq.getSourceReportId());
        }

        // 3. 제재 유형에 따른 로직 처리
        SanctionType sanctionType = sanctionReq.getSanctionType();
        sanctionType.apply(targetUser, sanctionReq.getDurationDays());

        // 4. 제재 기록 저장
        UserSanction userSanction = UserSanction.builder()
                .user(targetUser)
                .admin(adminUser)
                .sanctionType(sanctionType)
                .reason(sanctionReq.getReason())
                .expiresAt(targetUser.getSuspensionEndDate())
                .userReport(sourceReport)
                .build();

        sanctionRepository.save(userSanction);

        return SanctionRes.fromEntity(userSanction);
    }

    public Optional<UserSanction> findLatestUnread(User user) {
        return sanctionRepository.findTopByUserAndIsReadIsFalseOrderByCreatedAtDesc(user);
    }

    public Optional<UserSanction> findLatestUnread(User user, SanctionType type) {
        return sanctionRepository.findTopByUserAndSanctionTypeAndIsReadIsFalseOrderByCreatedAtDesc(user, type);
    }

}
