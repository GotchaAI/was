package Gotcha.domain.inquiry.controller;

import Gotcha.common.api.SuccessRes;
import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.inquiry.api.AdminInquiryApi;
import Gotcha.domain.inquiry.dto.AnswerReq;
import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.inquiry.service.AnswerService;
import Gotcha.domain.inquiry.service.InquiryService;
import Gotcha.domain.user.entity.User;
import Gotcha.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/qnas/")
@RequiredArgsConstructor
public class AdminInquiryController implements AdminInquiryApi {

    private final AnswerService answerService;

    private final InquiryService inquiryService;

    private final UserService userService;

    @Override
    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<?> createAnswer(@Valid @RequestBody AnswerReq answerReq, @PathVariable(value = "inquiryId") Long inquiryId,
                                          @AuthenticationPrincipal SecurityUserDetails securityUserDetails) {
        Inquiry inquiry = inquiryService.findInquiryById(inquiryId);
        User writer = userService.findUserByUserId(securityUserDetails.getId());
        answerService.createAnswer(answerReq, inquiry, writer);
        return ResponseEntity.ok(SuccessRes.from("QnA 답변 생성에 성공했습니다."));
    }
}
