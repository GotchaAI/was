package gotcha_common.constants;

public class SecurityConstants {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/webjars/**", "/error", "/api/v1/auth/**", "/api/v1/users/nickname-check",
            "/api/v1/notifications/**" , "/docs/**", "/ws-connect/**",  "/api/v1/ranking/**"
    };

    public static final String[] ADMIN_ENDPOINTS = {"/api/v1/admin/**"};
}
