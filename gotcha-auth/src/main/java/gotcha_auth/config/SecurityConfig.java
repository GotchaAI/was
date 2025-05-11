package gotcha_auth.config;

import gotcha_auth.filter.JwtAuthenticationFilter;
import gotcha_auth.filter.JwtExceptionFilter;
import gotcha_common.constants.SecurityConstants;
import gotcha_common.exception.CustomAccessDeniedHandler;
import gotcha_domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
//                .csrf(csrf -> csrf
//                        .requireCsrfProtectionMatcher(request -> {
//                            String path = request.getRequestURI();
//                            return path.equals("/api/v1/auth/token-reissue");
//                        })
//                        .csrfTokenRepository(csrfTokenRepository)
//                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .cors((cors) -> cors.configurationSource(corsConfigurationSource))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/v1/auth/guest/sign-up").authenticated()
                                .requestMatchers(SecurityConstants.PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(SecurityConstants.ADMIN_ENDPOINTS).hasAnyRole(String.valueOf(Role.ADMIN))
                                .anyRequest().authenticated()
                ).exceptionHandling(exception ->
                        exception.accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
