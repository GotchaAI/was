package gotcha_auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import gotcha_auth.filter.JwtAuthenticationFilter;
import gotcha_auth.filter.JwtExceptionFilter;
import gotcha_auth.jwt.JwtAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public JwtAuthenticationFilter authenticationFilter(JwtAuthService jwtAuthService) {
        return new JwtAuthenticationFilter(jwtAuthService, objectMapper);
    }

    @Bean
    public JwtExceptionFilter exceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }
}
