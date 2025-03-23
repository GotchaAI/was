package Gotcha.domain.user.service;

import Gotcha.common.exception.CustomException;
import Gotcha.domain.user.exceptionCode.UserExceptionCode;
import Gotcha.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if(userRepository.existsByNickname(nickname)) {
            throw new CustomException(UserExceptionCode.NICKNAME_EXIST);
        }
    }
}
