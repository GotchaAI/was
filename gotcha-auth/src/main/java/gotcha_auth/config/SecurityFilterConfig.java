package gotcha_auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gotcha_auth.auth.GuestDetailsService;
import gotcha_auth.auth.UserDetailsServiceImpl;

import gotcha_auth.filter.JwtAuthenticationFilter;
import gotcha_auth.filter.JwtExceptionFilter;
import gotcha_auth.jwt.BlackListTokenService;
import gotcha_auth.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final GuestDetailsService guestDetailsService;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final BlackListTokenService blackListTokenService;

    @Bean
    public JwtAuthenticationFilter authenticationFilter() {
        return new JwtAuthenticationFilter(userDetailsService, guestDetailsService, tokenProvider, blackListTokenService,objectMapper);
    }

    @Bean
    public JwtExceptionFilter exceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }
}
