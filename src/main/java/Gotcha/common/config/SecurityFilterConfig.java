package Gotcha.common.config;

import Gotcha.common.jwt.token.BlackListTokenService;
import Gotcha.common.jwt.auth.GuestDetailsService;
import Gotcha.common.jwt.token.TokenProvider;
import Gotcha.common.jwt.auth.UserDetailsServiceImpl;
import Gotcha.common.jwt.filter.JwtAuthenticationFilter;
import Gotcha.common.jwt.filter.JwtExceptionFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        return new JwtAuthenticationFilter(userDetailsService, guestDetailsService, tokenProvider, blackListTokenService);
    }

    @Bean
    public JwtExceptionFilter exceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }
}
