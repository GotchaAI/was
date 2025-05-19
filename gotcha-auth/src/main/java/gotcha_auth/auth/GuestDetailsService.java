package gotcha_auth.auth;

import gotcha_common.util.RedisUtil;
import gotcha_common.redis.RedisProperties;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestDetailsService implements UserDetailsService {
    private final RedisUtil redisUtil;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User guest = Optional.ofNullable((User) redisUtil.getData(RedisProperties.GUEST_KEY_PREFIX + username))
                .orElseThrow(()-> new UsernameNotFoundException("Guest not found : " + username));

        redisUtil.setDataExpire(RedisProperties.GUEST_KEY_PREFIX + username, RedisProperties.GUEST_TTL_SECONDS);

        return new SecurityUserDetails(guest);
    }
}
