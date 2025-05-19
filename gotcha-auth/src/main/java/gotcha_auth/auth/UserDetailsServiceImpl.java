package gotcha_auth.auth;

import gotcha_domain.auth.SecurityUserDetails;
import gotcha_domain.user.User;
import gotcha_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUuid(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + username));

        return new SecurityUserDetails(user);
    }
}