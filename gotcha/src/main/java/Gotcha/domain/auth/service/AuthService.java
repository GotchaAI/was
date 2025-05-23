package Gotcha.domain.auth.service;

import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import Gotcha.domain.auth.exception.AuthExceptionCode;
import Gotcha.domain.auth.util.RandomNicknameGenerator;
import gotcha_auth.dto.TokenDto;
import gotcha_auth.jwt.JwtHelper;
import gotcha_common.exception.CustomException;
import gotcha_common.exception.FieldValidationException;
import gotcha_common.util.RedisUtil;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.user.Role;
import gotcha_domain.user.User;
import gotcha_user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static gotcha_auth.jwt.JwtProperties.TOKEN_PREFIX;
import static gotcha_common.redis.RedisProperties.EMAIL_VERIFY_KEY_PREFIX;
import static gotcha_common.redis.RedisProperties.GUEST_KEY_PREFIX;
import static gotcha_common.redis.RedisProperties.GUEST_TTL_SECONDS;
import static gotcha_common.redis.RedisProperties.NICKNAME_VERIFY_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;
    private final RedisUtil redisUtil;

    @Transactional
    public TokenDto guestSignUp(SignUpReq signUpReq, SecurityUserDetails userDetails){
        if (!userDetails.getRole().equals(Role.GUEST)) {
            throw new CustomException(AuthExceptionCode.INVALID_GUEST);
        }

        validateSignUpInfo(signUpReq);

        Long guestId = userDetails.getId();

        User guest = Optional.ofNullable((User) redisUtil.getData(GUEST_KEY_PREFIX + guestId))
                .orElseThrow(()-> new CustomException(AuthExceptionCode.INVALID_USERID));

        String encodePassword = passwordEncoder.encode(signUpReq.password());
        User createdUser = userRepository.save(signUpReq.toEntityFromGuest(encodePassword, guest));

        redisUtil.deleteData(GUEST_KEY_PREFIX + guestId);
        return jwtHelper.createToken(createdUser, false);
    }

    @Transactional
    public TokenDto signUp(SignUpReq signUpReq) {
        validateSignUpInfo(signUpReq);

        redisUtil.deleteData(NICKNAME_VERIFY_KEY_PREFIX+signUpReq.nickname());
        redisUtil.deleteData(EMAIL_VERIFY_KEY_PREFIX + signUpReq.email());

        String encodePassword = passwordEncoder.encode(signUpReq.password());
        String uuid = generateUniqueUuid();
        User createdUser = userRepository.save(signUpReq.toEntity(encodePassword, uuid));
        return jwtHelper.createToken(createdUser, false);
    }

    @Transactional(readOnly = true)
    public TokenDto signIn(SignInReq signInReq){
        User user = userRepository.findByEmail(signInReq.email())
                .orElseThrow(() -> new CustomException(AuthExceptionCode.INVALID_USERNAME_AND_PASSWORD));

        if(!passwordEncoder.matches(signInReq.password(), user.getPassword())){
            throw new CustomException(AuthExceptionCode.INVALID_USERNAME_AND_PASSWORD);
        }

        return jwtHelper.createToken(user, signInReq.autoSignIn());
    }

    public void signOut(String HeaderAccessToken, String refreshToken, HttpServletResponse response) {
        String accessToken = HeaderAccessToken.substring(TOKEN_PREFIX.length()).trim();

        jwtHelper.removeToken(accessToken, refreshToken, response);
    }

    public TokenDto guestSignIn(){
        //무작위 아이디 값 생성
        String uuid = generateUniqueUuid();
        //무작위 닉네임 생성
        String nickname = RandomNicknameGenerator.generateNickname();

        //게스트 유저 생성
        User guestUser = User.builder()
                .uuid(uuid)
                .nickname(nickname)
                .role(Role.GUEST)
                .build();

        //게스트 유저 정보를 Redis에 저장
        redisUtil.setData(GUEST_KEY_PREFIX + uuid, guestUser);
        redisUtil.setDataExpire(GUEST_KEY_PREFIX + uuid, GUEST_TTL_SECONDS);

        //게스트 유저 토큰 생성
        return jwtHelper.createGuestToken(guestUser);
    }

    public TokenDto reissueAccessToken(String refreshToken) {
        return jwtHelper.reissueToken(refreshToken);
    }

    public void validateSignUpInfo(SignUpReq signUpReq){
        Map<String, String> fieldErrors = new HashMap<>();

        if(!signUpReq.validatePasswordMatch()){
            fieldErrors.put("password", "비밀번호가 일치하지 않습니다.");
        }

        if(!redisUtil.existed(NICKNAME_VERIFY_KEY_PREFIX+signUpReq.nickname())){
            fieldErrors.put("nickname", "닉네임 중복 확인이 완료되지 않았습니다.");
        }

        if (!redisUtil.existed(EMAIL_VERIFY_KEY_PREFIX + signUpReq.email())) {
            fieldErrors.put("email", "이메일 인증이 완료되지 않았습니다.");
        }

        if (!fieldErrors.isEmpty()) {
            throw new FieldValidationException(fieldErrors);
        }
    }

    public String generateUniqueUuid() {
        String shortUuid;
        do {
            UUID uuid = UUID.randomUUID();
            ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());

            shortUuid = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(buffer.array())
                    .substring(0, 8);

        } while (userRepository.existsByUuid(shortUuid) || redisUtil.existed(GUEST_KEY_PREFIX + shortUuid));
        return shortUuid;
    }
}
