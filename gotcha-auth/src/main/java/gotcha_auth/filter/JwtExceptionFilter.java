package gotcha_auth.filter;

import gotcha_auth.exception.JwtExceptionCode;
import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.ExceptionRes;
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
import org.springframework.security.authentication.AuthenticationServiceException;
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
            handleTokenException(response, JwtExceptionCode.ACCESS_TOKEN_EXPIRED, HttpServletResponse.SC_UNAUTHORIZED);
        } catch (SignatureException e) {
            handleTokenException(response, JwtExceptionCode.INVALID_TOKEN_SIGNATURE, HttpServletResponse.SC_UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            handleTokenException(response, JwtExceptionCode.INVALID_ACCESS_TOKEN, HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException e) {
            handleTokenException(response, JwtExceptionCode.UNKNOWN_TOKEN_ERROR, HttpServletResponse.SC_UNAUTHORIZED);
        } catch (AuthenticationServiceException e){
            handleTokenException(response, JwtExceptionCode.ACCESS_TOKEN_NOT_FOUND, HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleTokenException(HttpServletResponse response, ExceptionCode exceptionCode, int statusCode) throws  IOException {
        ExceptionRes exceptionRes = ExceptionRes.from(exceptionCode);
        String message = objectMapper.writeValueAsString(exceptionRes);

        log.error("[Token Exception] {}", exceptionCode.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        response.getWriter().write(message);
    }
}