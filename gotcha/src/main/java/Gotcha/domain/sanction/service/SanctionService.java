package Gotcha.domain.sanction.service;

import Gotcha.domain.auth.exception.AuthExceptionCode;
import Gotcha.domain.auth.exception.UserAccountStatusException;
import Gotcha.domain.report.service.UserReportService;
import Gotcha.domain.sanction.dto.SanctionReq;
import Gotcha.domain.sanction.dto.SanctionRes;
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

import java.util.HashMap;
import java.util.Map;
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
                .expireDuration(sanctionReq.getDurationDays())
                .userReport(sourceReport)
                .build();

        sanctionRepository.save(userSanction);

        return SanctionRes.fromEntity(userSanction);
    }

    @Transactional
    public void validateLoginAccess(User user) {
        if (user.getUserStatus() == UserStatus.SUSPENDED || user.getUserStatus() == UserStatus.BANNED) {
            AuthExceptionCode code = user.getUserStatus() == UserStatus.SUSPENDED ?
                    AuthExceptionCode.ACCOUNT_SUSPENDED : AuthExceptionCode.ACCOUNT_BANNED;

            UserSanction sanction = findLatestUnread(user)
                    .orElseThrow(()->new CustomException(AuthExceptionCode.SANCTION_NOT_FOUND));
            sanction.markAsRead();

            Map<String, Object> details = new HashMap<>();
            details.put("reason", sanction.getReason());
            if (sanction.getExpireDuration() != null) {
                details.put("expireDuration", sanction.getExpireDuration());
            }
            throw new UserAccountStatusException(code, details);
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

    public Optional<UserSanction> findLatestUnread(User user) {
        return sanctionRepository.findTopByUserAndIsReadIsFalseOrderByCreatedAtDesc(user);
    }

    public Optional<UserSanction> findLatestUnread(User user, SanctionType type) {
        return sanctionRepository.findTopByUserAndSanctionTypeAndIsReadIsFalseOrderByCreatedAtDesc(user, type);
    }

}
