package Gotcha.common.jwt;

import Gotcha.common.util.RedisUtil;
import Gotcha.domain.user.entity.User;
import Gotcha.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static Gotcha.common.redis.RedisProperties.GUEST_TTL_SECONDS;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

        return new SecurityUserDetails(user);
    }

    public UserDetails loadGuestByUserId(Long guestId) throws UsernameNotFoundException{
        User guest = Optional.ofNullable((User) redisUtil.getData("guest:" + guestId))
                .orElseThrow(()-> new UsernameNotFoundException("Guest not found : " + guestId));

        redisUtil.setDataExpire("guest:" + guestId, GUEST_TTL_SECONDS);

        return new SecurityUserDetails(guest);
    }
}