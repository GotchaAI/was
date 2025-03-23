package Gotcha.domain.auth.service;

import Gotcha.common.exception.CustomException;
import Gotcha.common.jwt.JwtHelper;
import Gotcha.common.util.RedisUtil;
import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import Gotcha.domain.auth.dto.TokenDto;
import Gotcha.domain.auth.exception.AuthExceptionCode;
import Gotcha.domain.user.entity.User;
import Gotcha.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static Gotcha.common.redis.RedisProperties.EMAIL_VERIFY_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;
    private final RedisUtil redisUtil;

    @Transactional
    public TokenDto signUp(SignUpReq signUpReq) {
        signUpReq.validatePasswordMatch();

        if (!redisUtil.existed(EMAIL_VERIFY_KEY_PREFIX + signUpReq.email())) {
            throw new CustomException(AuthExceptionCode.NOT_VERIFIED_EMAIL);
        }

        String encodePassword = passwordEncoder.encode(signUpReq.password());
        User createdUser = userRepository.save(signUpReq.toEntity(encodePassword));
        return jwtHelper.createToken(createdUser);
    }

    @Transactional(readOnly = true)
    public TokenDto signIn(SignInReq signInReq){
        User user = userRepository.findByEmail(signInReq.email())
                .orElseThrow(() -> new CustomException(AuthExceptionCode.INVALID_USERNAME_AND_PASSWORD));

        if(!passwordEncoder.matches(signInReq.password(), user.getPassword())){
            throw new CustomException(AuthExceptionCode.INVALID_USERNAME_AND_PASSWORD);
        }

        return jwtHelper.createToken(user);
    }

    public TokenDto reissueAccessToken(String refreshToken) {
        return jwtHelper.reissueToken(refreshToken);
    }
}
