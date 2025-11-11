package Gotcha.domain.sanction.controller;

import Gotcha.domain.sanction.dto.SanctionReq;
import Gotcha.domain.sanction.dto.SanctionRes;
import Gotcha.domain.sanction.service.SanctionService;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/admin/sanctions")
@RequiredArgsConstructor
public class SanctionController {

    private final SanctionService sanctionService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanctionRes> applySanction(
            @Valid @RequestBody SanctionReq sanctionReq,
       @AuthenticationPrincipal SecurityUserDetails userDetails) {

        String adminId = userDetails.getUuid();

        SanctionRes response = sanctionService.sanctionUser(sanctionReq, adminId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
