package Gotcha.domain.user.service;

import Gotcha.common.exception.CustomException;
import Gotcha.common.util.RedisUtil;
import Gotcha.domain.user.dto.UserInfoRes;
import Gotcha.domain.user.entity.Role;
import Gotcha.domain.user.entity.User;
import Gotcha.domain.user.exceptionCode.UserExceptionCode;
import Gotcha.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static Gotcha.common.redis.RedisProperties.GUEST_KEY_PREFIX;
import static Gotcha.common.redis.RedisProperties.NICKNAME_VERIFY_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    private static final long NICKNAME_VERIFY_EXPIRATION_TIME = 10 * 60;

    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(UserExceptionCode.NICKNAME_EXIST);
        }

        String verifiedKey = NICKNAME_VERIFY_KEY_PREFIX + nickname;
        redisUtil.setData(verifiedKey, "true");
        redisUtil.setDataExpire(verifiedKey, NICKNAME_VERIFY_EXPIRATION_TIME);
    }

    @Transactional(readOnly = true)
    public void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(UserExceptionCode.EMAIL_EXIST);
        }
    }

    @Transactional(readOnly = true)
    public UserInfoRes getUserInfo(Long userId, Role role){
        User user = switch (role){
            case GUEST -> findGuestByGuestId(userId);
            case USER,ADMIN -> findUserByUserId(userId);
            default -> throw new CustomException(UserExceptionCode.INVALID_USERID);
        };
        return UserInfoRes.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public User findUserByUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new CustomException(UserExceptionCode.INVALID_USERID));
    }

    private User findGuestByGuestId(Long guestId){
        return Optional.ofNullable((User) redisUtil.getData(GUEST_KEY_PREFIX + guestId))
                .orElseThrow(()-> new CustomException(UserExceptionCode.INVALID_USERID));
    }
}
