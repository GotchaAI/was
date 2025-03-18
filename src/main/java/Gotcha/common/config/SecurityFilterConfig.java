package Gotcha.common.config;

import Gotcha.common.jwt.BlackListTokenService;
import Gotcha.common.jwt.TokenProvider;
import Gotcha.common.jwt.filter.JwtAuthenticationFilter;
import Gotcha.common.jwt.filter.JwtExceptionFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterConfig {
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final BlackListTokenService blackListTokenService;

    @Bean
    public JwtAuthenticationFilter authenticationFilter() {
        return new JwtAuthenticationFilter(userDetailsService, tokenProvider, blackListTokenService);
    }

    @Bean
    public JwtExceptionFilter exceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }
}
