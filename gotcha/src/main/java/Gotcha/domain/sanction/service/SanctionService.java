package Gotcha.domain.sanction.service;

import Gotcha.domain.report.service.UserReportService;
import Gotcha.domain.sanction.dto.SanctionReq;
import Gotcha.domain.sanction.dto.SanctionRes;
import Gotcha.domain.sanction.exception.SanctionExceptionCode;
import Gotcha.domain.sanction.repository.SanctionRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.report.UserReport;
import gotcha_domain.sanction.SanctionType;
import gotcha_domain.sanction.UserSanction;
import gotcha_domain.user.User;
import gotcha_domain.user.UserStatus;
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
        User targetUser = userService.findUserByUuid(sanctionReq.getTargetUserUuId());

        // 2. source Report 조회
        UserReport sourceReport = userReportService.findUserReportById(sanctionReq.getSourceReportId());
        String reason = sourceReport.getUserReportType().getReason();

        // 3. 제재 유형에 따른 로직 처리
        SanctionType sanctionType = sanctionReq.getSanctionType();
        if(sanctionType == SanctionType.TEMP_BAN_CANCEL) {
            if(targetUser.getUserStatus() != UserStatus.SUSPENDED) {
                throw new CustomException(SanctionExceptionCode.NOT_SUSPENDED_USER);
            }
        }
        sanctionType.apply(targetUser, sanctionReq.getDurationDays());

        // 4. 제재 기록 저장
        UserSanction userSanction = UserSanction.builder()
                .user(targetUser)
                .admin(adminUser)
                .sanctionType(sanctionType)
                .reason(reason)
                .expireDuration(sanctionReq.getDurationDays())
                .userReport(sourceReport)
                .build();

        sanctionRepository.save(userSanction);

        return SanctionRes.fromEntity(userSanction);
    }

    @Transactional
    public void validateSuspendedEndDate(User user) {
        if(user.getUserStatus()==UserStatus.SUSPENDED) {
            user.checkSuspensionAndUnsuspend();
        }
    }

    @Transactional
    public Optional<SanctionRes> findAndMarkUnreadWarning(User user) {
        return findLatestUnread(user, SanctionType.WARNING)
                .map(warning -> {
                    warning.markAsRead();
                    return SanctionRes.fromEntity(warning);
                });
    }

    public Optional<UserSanction> findLatestUnread(User user, SanctionType type) {
        return sanctionRepository.findTopByUserAndSanctionTypeAndIsReadIsFalseOrderByCreatedAtDesc(user, type);
    }

}
