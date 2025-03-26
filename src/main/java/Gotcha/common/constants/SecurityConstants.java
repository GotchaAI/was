package Gotcha.common.constants;

public class SecurityConstants {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/webjars/**", "/error", "/api/v1/auth/**", "/api/v1/users/nickname-check", "/api/v1/guest/sign-in"
    };

    public static final String[] ADMIN_ENDPOINTS = {"/api/v1/admin/**"};
}
