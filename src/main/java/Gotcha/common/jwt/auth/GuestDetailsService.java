package Gotcha.common.jwt.auth;

import Gotcha.common.util.RedisUtil;
import Gotcha.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static Gotcha.common.redis.RedisProperties.GUEST_TTL_SECONDS;

@Service
@RequiredArgsConstructor
public class GuestDetailsService implements UserDetailsService {
    private final RedisUtil redisUtil;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Long guestId;
        try {
            guestId = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid guest ID: " + username);
        }

        User guest = Optional.ofNullable((User) redisUtil.getData("guest:" + guestId))
                .orElseThrow(()-> new UsernameNotFoundException("Guest not found : " + guestId));

        redisUtil.setDataExpire("guest:" + guestId, GUEST_TTL_SECONDS);

        return new SecurityUserDetails(guest);
    }
}
