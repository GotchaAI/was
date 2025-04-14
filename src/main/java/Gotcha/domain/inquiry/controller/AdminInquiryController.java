package Gotcha.domain.inquiry.controller;

import Gotcha.common.api.SuccessRes;
import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.inquiry.api.AdminInquiryApi;
import Gotcha.domain.inquiry.dto.AnswerReq;
import Gotcha.domain.inquiry.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/qnas/")
@RequiredArgsConstructor
public class AdminInquiryController implements AdminInquiryApi {

    private final AnswerService answerService;

    @Override
    @PostMapping("/{inquiryId}/answer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAnswer(@Valid @RequestBody AnswerReq answerReq, @PathVariable(value = "inquiryId") Long inquiryId,
                                          @AuthenticationPrincipal SecurityUserDetails securityUserDetails) {
        answerService.createAnswer(answerReq, inquiryId, securityUserDetails.getId());
        return ResponseEntity.ok(SuccessRes.from("QnA 답변 생성에 성공했습니다."));
    }
}
