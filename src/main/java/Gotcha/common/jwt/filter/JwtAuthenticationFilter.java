package Gotcha.common.jwt.filter;

import Gotcha.common.constants.SecurityConstants;
import Gotcha.common.exception.ExceptionRes;
import Gotcha.common.exception.exceptionCode.ExceptionCode;
import Gotcha.common.exception.exceptionCode.GlobalExceptionCode;
import Gotcha.common.jwt.token.BlackListTokenService;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.jwt.token.TokenProvider;
import Gotcha.domain.user.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;

import static Gotcha.common.jwt.token.JwtProperties.ACCESS_HEADER_VALUE;
import static Gotcha.common.jwt.token.JwtProperties.TOKEN_PREFIX;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final UserDetailsService guestDetailsService;
    private final TokenProvider tokenProvider;
    private final BlackListTokenService blackListTokenService;
    private final ObjectMapper objectMapper;


    private static final String SPECIAL_CHARACTERS_PATTERN = "[`':;|~!@#$%()^&*+=?/{}\\[\\]\\\"\\\\\"]+$";
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(
            @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
            @Qualifier("guestDetailsService") UserDetailsService guestDetailsService,
            TokenProvider tokenProvider,
            BlackListTokenService blackListTokenService,
            ObjectMapper objectMapper
    ) {
        this.userDetailsService = userDetailsService;
        this.guestDetailsService = guestDetailsService;
        this.tokenProvider = tokenProvider;
        this.blackListTokenService = blackListTokenService;
        this.objectMapper = objectMapper;
    }




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenHeader = request.getHeader(ACCESS_HEADER_VALUE);

        if (isPublicResource(request.getRequestURI())
                && !request.getRequestURI().equals("/api/v1/auth/guest/sign-up")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            throw new AuthenticationServiceException(JwtExceptionCode.ACCESS_TOKEN_NOT_FOUND.getMessage());
        }

        try{
            String accessToken = resolveAccessToken(response, accessTokenHeader);

            String role = tokenProvider.getRole(accessToken);
            UserDetails userDetails;
            if (role.equals(String.valueOf(Role.GUEST))) {
                Long guestId = tokenProvider.getUserId(accessToken);
                userDetails = guestDetailsService.loadUserByUsername(guestId.toString());
            }
            else{
                String username = tokenProvider.getUsername(accessToken);
                userDetails = userDetailsService.loadUserByUsername(username);
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e){
            log.warn("[사용자 인증 실패] {}", e.getMessage());

            ExceptionCode exceptionCode = GlobalExceptionCode.USER_NOT_FOUND;

            response.setStatus(exceptionCode.getStatus().value());
            response.setContentType("application/json;charset=UTF-8");

            objectMapper.writeValue(response.getWriter(), ExceptionRes.from(exceptionCode));
        }
    }

    private String resolveAccessToken(HttpServletResponse response, String accessTokenGetHeader) throws IOException {
        String accessToken = accessTokenGetHeader.substring(TOKEN_PREFIX.length()).trim();

        accessToken = accessToken.replaceAll(SPECIAL_CHARACTERS_PATTERN, "");

        if (tokenProvider.isExpired(accessToken)) {
            throw new ExpiredJwtException(null, null, JwtExceptionCode.ACCESS_TOKEN_EXPIRED.getMessage());
        }

        if (blackListTokenService.existsBlackListCheck(accessToken)) {
            throw new ExpiredJwtException(null, null, JwtExceptionCode.ACCESS_TOKEN_EXPIRED.getMessage());
        }
        return accessToken;
    }

    private boolean isPublicResource(String uri) {
        return Arrays.stream(SecurityConstants.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> antPathMatcher.match(pattern, uri));
    }
}

