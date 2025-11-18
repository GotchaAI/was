package Gotcha.domain.sanction.service;

import static Gotcha.domain.sanction.exception.SanctionExceptionCode.INVALID_PAGE_FOR_SEARCH;

import Gotcha.domain.report.service.UserReportService;
import Gotcha.domain.sanction.dto.SanctionReq;
import Gotcha.domain.sanction.dto.SanctionRes;
import Gotcha.domain.sanction.dto.SanctionUserListRes;
import Gotcha.domain.sanction.exception.SanctionExceptionCode;
import Gotcha.domain.sanction.repository.SanctionRepository;
import gotcha_common.exception.CustomException;
import gotcha_domain.report.UserReport;
import gotcha_domain.sanction.SanctionType;
import gotcha_domain.sanction.UserSanction;
import gotcha_domain.user.User;
import gotcha_domain.user.UserStatus;
import gotcha_user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SanctionService {
    private final SanctionRepository sanctionRepository;
    private final UserService userService;
    private final UserReportService userReportService;
    private final Integer USERS_PER_PAGE = 6;

    @Transactional(readOnly = true)
    public Page<SanctionUserListRes> getSanctionUsers(String nickname, int page) {
        boolean isSearchModeByNickname = nickname != null && !nickname.isBlank();

        Page<User> users = isSearchModeByNickname
                ? searchUser(nickname, page)
                : findAllUsers(page);

        return users.map(this::toResponse);
    }

    private Page<User> searchUser(String nickname, int page) {
        if (page != 0) {
            throw new CustomException(INVALID_PAGE_FOR_SEARCH);
        }

        User user = userService.findUserByNickname(nickname);
        return new PageImpl<>(List.of(user), PageRequest.of(0, USERS_PER_PAGE), 1);
    }

    private Page<User> findAllUsers(int page) {
        return userService.findAllUsers( PageRequest.of(page, USERS_PER_PAGE));
    }


    private SanctionUserListRes toResponse(User user) {
        return new SanctionUserListRes(
                user.getNickname(),
                user.getCreatedAt().toLocalDate(),
                user.getEmail(),
                userReportService.countReportByUserId(user.getId()),
                user.getWarningCount()
        );
    }


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
