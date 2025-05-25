package gotcha_auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import gotcha_auth.jwt.JwtAuthService;
import gotcha_auth.jwt.JwtProperties;
import gotcha_common.constants.SecurityConstants;
import gotcha_common.exception.ExceptionRes;
import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtAuthService jwtAuthService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenHeader = request.getHeader(JwtProperties.ACCESS_HEADER_VALUE);

        if (isPublicResource(request.getMethod(), request.getRequestURI(), accessTokenHeader)
                && !request.getRequestURI().equals("/api/v1/auth/guest/sign-up")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication auth = jwtAuthService.authenticate(accessTokenHeader);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            log.warn("[사용자 인증 실패] {}", e.getMessage());

            ExceptionCode exceptionCode = GlobalExceptionCode.USER_NOT_FOUND;

            response.setStatus(exceptionCode.getStatus().value());
            response.setContentType("application/json;charset=UTF-8");

            objectMapper.writeValue(response.getWriter(), ExceptionRes.from(exceptionCode));
        }
    }

    private boolean isPublicResource(String method, String uri, String accessToken) {
        boolean uriOnlyMatch = Arrays.stream(SecurityConstants.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> antPathMatcher.match(pattern, uri));

        boolean methodUriMatch = Arrays.stream(SecurityConstants.METHOD_BASED_PUBLIC_ENDPOINTS)
                .anyMatch(entry -> method.equalsIgnoreCase(entry[0]) &&
                        (antPathMatcher.match(entry[1], uri) && !antPathMatcher.match("/api/v1/qnas/mine", uri)));

        return uriOnlyMatch || (methodUriMatch && (accessToken == null || accessToken.isBlank()));
    }

}

