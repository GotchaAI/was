package gotcha_user.service;

import gotcha_common.exception.CustomException;
import gotcha_common.util.RedisUtil;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.user.ChatOption;
import gotcha_domain.user.PrivateChatOption;
import gotcha_domain.user.Role;
import gotcha_domain.user.User;
import gotcha_user.dto.UserInfoRes;
import gotcha_user.exceptionCode.UserExceptionCode;
import gotcha_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static gotcha_common.redis.RedisProperties.GUEST_KEY_PREFIX;
import static gotcha_common.redis.RedisProperties.NICKNAME_VERIFY_KEY_PREFIX;

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
    public UserInfoRes getUserInfo(SecurityUserDetails userDetails){
        Role role = userDetails.getRole();
        User user = switch (role){
            case GUEST -> findGuestByGuestId(userDetails.getUuid());
            case USER,ADMIN -> findUserByUserId(userDetails.getId());
            default -> throw new CustomException(UserExceptionCode.INVALID_USERID);
        };
        return UserInfoRes.fromEntity(user);
    }

    @Transactional
    public void changeNickname(Long userId, String nickname) {
        if (!"true".equals(redisUtil.getData(NICKNAME_VERIFY_KEY_PREFIX + nickname))) {
            throw new CustomException(UserExceptionCode.NICKNAME_NOT_VERIFIED);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(UserExceptionCode.NICKNAME_EXIST);
        }

        User user = findUserByUserId(userId);
        if (user.getNickname().equals(nickname)) {
            throw new CustomException(UserExceptionCode.SAME_NICKNAME);
        }

        user.changeNickname(nickname);
    }

    @Transactional(readOnly = true)
    public User findUserByUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()->new CustomException(UserExceptionCode.INVALID_USERID));
    }

    private User findGuestByGuestId(String uuid){
        return Optional.ofNullable((User) redisUtil.getData(GUEST_KEY_PREFIX + uuid))
                .orElseThrow(()-> new CustomException(UserExceptionCode.INVALID_USERID));
    }

    @Transactional(readOnly = true)
    public User findUserByNickname(String nickname){
        return userRepository.findByNickname(nickname)
                .orElseThrow(()->new CustomException(UserExceptionCode.INVALID_USERID));
    }

    @Transactional(readOnly = true)
    public User findUserByNicknameWithFriends(String nickname) {
        return userRepository.findByNicknameWithFriends(nickname)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));
    }

    @Transactional(readOnly = true)
    public User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));
    }

    @Transactional(readOnly = true)
    public User findUserByUuidWithFriends(String uuid) {
        return userRepository.findByUuidWithFriends(uuid)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));
    }

    @Transactional
    public void updateLastLogout(User user, LocalDateTime accessTokenExpiredAt) {
        if(user.getRole()==Role.GUEST)
            return;
        user.setLastLogout(accessTokenExpiredAt);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserExp(User user, Long exp) {
        user.setExp(exp);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserLevel(User user, int level) {
        user.setLevel(level);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findUserListByKeyword(String keyword) {
        return userRepository.findByNicknameContaining(keyword);
    }

    @Transactional
    public void updateUserChatSetting(Long userId, ChatOption chatOption, PrivateChatOption privateChatOption) {
        User user = findUserByUserId(userId);
        user.updateChatSettings(chatOption, privateChatOption);
    }

    public User getUserByUuidAllowingGuest(String uuid) {
        if (uuid == null) {
            throw new CustomException(UserExceptionCode.INVALID_USERID);
        }

        Optional<User> dbUser = userRepository.findByUuid(uuid);
        if (dbUser.isPresent()) return dbUser.get();

        User guest = (User) redisUtil.getData(GUEST_KEY_PREFIX + uuid);
        if (guest != null) return guest;

        throw new CustomException(UserExceptionCode.INVALID_USERID);
    }
}
