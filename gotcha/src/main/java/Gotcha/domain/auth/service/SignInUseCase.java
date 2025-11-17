package Gotcha.domain.auth.service;

import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.sanction.dto.SanctionRes;
import Gotcha.domain.sanction.service.SanctionService;
import gotcha_auth.dto.TokenDto;
import gotcha_auth.jwt.JwtHelper;
import gotcha_domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignInUseCase {
    private final AuthService authService;
    private final SanctionService sanctionService;
    private final JwtHelper jwtHelper;

    @Transactional
    public TokenDto execute(SignInReq signInReq) {
        // 1. 사용자 인증
        User user = authService.authenticate(signInReq);

        // 2. 정지기간 만료되었는지 확인
        sanctionService.validateSuspendedEndDate(user);

        // 3. 제재/차단 상태 확인
        sanctionService.validateLoginAccess(user);

        // 4. 경고 확인
        Optional<SanctionRes> warningOpt = sanctionService.findAndMarkUnreadWarning(user);

        // 5. 토큰 생성
        TokenDto tokenDto = jwtHelper.createToken(user, signInReq.autoSignIn());

        // 6. 경고 메시지가 있으면 토큰에 추가하여 반환
        return warningOpt
                .map(warning -> TokenDto.of(tokenDto.accessToken(), tokenDto.refreshToken(), tokenDto.accessTokenExpiredAt(), tokenDto.autoSignIn(), warning))
                .orElse(tokenDto);
    }
}
