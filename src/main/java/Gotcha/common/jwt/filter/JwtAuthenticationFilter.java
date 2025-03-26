package Gotcha.common.jwt.filter;

import Gotcha.common.constants.SecurityConstants;
import Gotcha.common.jwt.BlackListTokenService;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.jwt.TokenProvider;
import Gotcha.common.jwt.userDetails.GuestUserDetails;
import Gotcha.domain.guestUser.entity.GuestUser;
import Gotcha.domain.guestUser.service.GuestUserService;
import Gotcha.domain.user.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;

import static Gotcha.common.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static Gotcha.common.jwt.JwtProperties.TOKEN_PREFIX;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final BlackListTokenService blackListTokenService;
    private final GuestUserService guestUserService;

    private static final String SPECIAL_CHARACTERS_PATTERN = "[`':;|~!@#$%()^&*+=?/{}\\[\\]\\\"\\\\\"]+$";
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenHeader = request.getHeader(ACCESS_HEADER_VALUE);

        if(isPublicResource(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            throw new AuthenticationServiceException(JwtExceptionCode.ACCESS_TOKEN_NOT_FOUND.getMessage());
        }

        String accessToken = resolveAccessToken(response, accessTokenHeader);

        String role = tokenProvider.getRole(accessToken);
        UserDetails userDetails;

        if (String.valueOf(Role.GUEST).equals(role)) {
            Long guestId = tokenProvider.getUserId(accessToken);
            GuestUser guestUser = guestUserService.getGuestUser(guestId);
            userDetails = new GuestUserDetails(guestUser);
        } else {
            String username = tokenProvider.getEmail(accessToken);
            userDetails = userDetailsService.loadUserByUsername(username);
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
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

