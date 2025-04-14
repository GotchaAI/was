package Gotcha.domain.inquiry.controller;

import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.inquiry.api.InquiryApi;
import Gotcha.domain.inquiry.dto.InquiryReq;
import Gotcha.domain.inquiry.dto.InquirySortType;
import Gotcha.domain.inquiry.dto.InquirySummaryRes;
import Gotcha.domain.inquiry.service.InquiryService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getInquiryById(Long inquiryId) {
        return null;
    }

    @Override
    public ResponseEntity<?> createInquiry(InquiryReq inquiryReq, SecurityUserDetails userDetails) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateInquiry(Long inquiryId, InquiryReq inquiryReq, SecurityUserDetails userDetails) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteInquiry(Long inquiryId, SecurityUserDetails userDetails) {
        return null;
    }
}
