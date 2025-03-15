package Gotcha.common.jwt.filter;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import Gotcha.common.exception.ExceptionRes;
import Gotcha.common.jwt.BlackListTokenService;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import Gotcha.common.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;
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

        if (accessToken == null) {
            return;
        }

        String username = tokenProvider.getEmail(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private String resolveAccessToken(HttpServletResponse response, String accessTokenGetHeader) throws IOException {
        String accessToken = accessTokenGetHeader.substring(TOKEN_PREFIX.length()).trim();

        accessToken = accessToken.replaceAll(SPECIAL_CHARACTERS_PATTERN, "");

        if (tokenProvider.isExpired(accessToken)) {      // 만료되었는지
            handleExceptionToken(response, JwtExceptionCode.ACCESS_TOKEN_EXPIRED);
            return null;
        }

        if (blackListTokenService.existsBlackListCheck(accessToken)) {
            handleExceptionToken(response, JwtExceptionCode.BLACKLIST_ACCESS_TOKEN);
            return null;
        }
        return accessToken;
    }

    private void handleExceptionToken(HttpServletResponse response, ExceptionCode exceptionCode) throws IOException {

        ExceptionRes exceptionsRes = ExceptionRes.from(exceptionCode);
        String messageBody = objectMapper.writeValueAsString(exceptionsRes);

        log.error("Error occurred: {}", exceptionCode.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(exceptionCode.getStatus());
        response.getWriter().write(messageBody);
    }
}

