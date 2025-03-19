package Gotcha.common.jwt.filter;

import Gotcha.common.exception.exceptionCode.ExceptionCode;
import Gotcha.common.exception.ExceptionRes;
import Gotcha.common.jwt.exception.JwtExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            handleTokenException(response, JwtExceptionCode.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            handleTokenException(response, JwtExceptionCode.INVALID_TOKEN_SIGNATURE);
        } catch (MalformedJwtException e) {
            handleTokenException(response, JwtExceptionCode.INVALID_ACCESS_TOKEN);
        } catch (JwtException e) {
            handleTokenException(response, JwtExceptionCode.UNKNOWN_TOKEN_ERROR);
        }
    }

    private void handleTokenException(HttpServletResponse response, ExceptionCode exceptionCode) throws  IOException {
        ExceptionRes exceptionRes = ExceptionRes.from(exceptionCode);
        String message = objectMapper.writeValueAsString(exceptionRes);

        log.error("[Token Exception] {}", exceptionCode.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }
}