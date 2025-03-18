package Gotcha.common.jwt.filter;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import Gotcha.common.exception.ExceptionRes;
import Gotcha.common.jwt.BlackListTokenService;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static Gotcha.common.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static Gotcha.common.jwt.JwtProperties.TOKEN_PREFIX;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final BlackListTokenService blackListTokenService;

    private static final String SPECIAL_CHARACTERS_PATTERN = "[`':;|~!@#$%()^&*+=?/{}\\[\\]\\\"\\\\\"]+$";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenHeader = request.getHeader(ACCESS_HEADER_VALUE);

        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveAccessToken(response, accessTokenHeader);

        String username = tokenProvider.getEmail(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

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
}

