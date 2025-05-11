package gotcha_common.exception;

import gotcha_common.exception.exceptionCode.ExceptionCode;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("[AccessDeniedException] {} occurred", accessDeniedException.getClass().getSimpleName());

        ExceptionCode exceptionCode;
        if (accessDeniedException instanceof InvalidCsrfTokenException ||
                accessDeniedException instanceof MissingCsrfTokenException) {
            exceptionCode = GlobalExceptionCode.CSRF_INVALID;
        } else {
            exceptionCode = GlobalExceptionCode.ACCESS_DENIED;
        }

        response.setStatus(exceptionCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), ExceptionRes.from(exceptionCode));
    }
}