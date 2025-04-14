package Gotcha.domain.inquiry.controller;

import Gotcha.common.api.SuccessRes;
import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.inquiry.api.InquiryApi;
import Gotcha.domain.inquiry.dto.InquiryReq;
import Gotcha.domain.inquiry.dto.InquiryRes;
import Gotcha.domain.inquiry.dto.InquirySortType;
import Gotcha.domain.inquiry.dto.InquirySummaryRes;
import Gotcha.domain.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qnas")
@RequiredArgsConstructor
public class InquiryController implements InquiryApi {

    private final InquiryService inquiryService;

    @Override
    @GetMapping
    public ResponseEntity<?> getInquiries(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                   @RequestParam(value = "sort", defaultValue = "DATE_DESC")InquirySortType sort,
                                   @RequestParam(value = "isSolved", required = false) Boolean isSolved) {
        Page<InquirySummaryRes> inquiries = inquiryService.getInquiries(keyword, page, sort, isSolved);

        return ResponseEntity.status(HttpStatus.OK).body(inquiries);
    }

    @Override
    @GetMapping("/mine")
    public ResponseEntity<?> getMyInquiries(@RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                            @RequestParam(value = "sort", defaultValue = "DATE_DESC") InquirySortType sort,
                                            @RequestParam(value = "isSolved", required = false) Boolean isSolved,
                                            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        Page<InquirySummaryRes> myInquiries = inquiryService.getMyInquiries(keyword, page, sort, isSolved, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(myInquiries);
    }

    @Override
    @GetMapping("/{inquiryId}")
    public ResponseEntity<?> getInquiryById(@PathVariable(value = "inquiryId") Long inquiryId) {
        InquiryRes inquiryRes = inquiryService.getInquiryById(inquiryId);

        return ResponseEntity.status(HttpStatus.OK).body(inquiryRes);
    }

    @Override
    @PostMapping
    public ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryReq inquiryReq,
                                           @AuthenticationPrincipal SecurityUserDetails userDetails) {
        inquiryService.createInquiry(inquiryReq, userDetails.getId());
        return ResponseEntity.ok(SuccessRes.from("QnA 생성에 성공했습니다."));
    }

    @Override
    @PutMapping("/{inquiryId}")
    public ResponseEntity<?> updateInquiry(@PathVariable(value = "inquiryId") Long inquiryId,
                                           @Valid @RequestBody InquiryReq inquiryReq,
                                           @AuthenticationPrincipal SecurityUserDetails userDetails) {
        inquiryService.updateInquiry(inquiryReq, inquiryId, userDetails.getId());
        return ResponseEntity.ok(SuccessRes.from("QnA 수정에 성공했습니다."));
    }

    @Override
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable(value = "inquiryId") Long inquiryId,
                                           @AuthenticationPrincipal SecurityUserDetails userDetails) {
        inquiryService.deleteInquiry(inquiryId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
