package Gotcha.domain.auth.controller;

import Gotcha.domain.auth.dto.CsrfTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "CSRF", description = "CSRF 토큰 발급")
public class CsrfController {

    @GetMapping("/csrf-token")
    @Operation(summary = "CSRF 토큰 발급 API")
    public ResponseEntity<CsrfTokenResponse> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            return ResponseEntity.ok(new CsrfTokenResponse(csrfToken.getToken()));
        }
        return ResponseEntity.internalServerError().build();
    }
}
