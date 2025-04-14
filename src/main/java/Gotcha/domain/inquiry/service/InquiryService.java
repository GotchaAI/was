package Gotcha.domain.inquiry.service;

import Gotcha.domain.inquiry.dto.InquirySortType;
import Gotcha.domain.inquiry.dto.InquirySummaryRes;
import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.inquiry.repository.InquiryRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    private final Integer INQUIRIES_PER_PAGE = 10;

    @Transactional(readOnly = true)
    public Page<InquirySummaryRes> getInquiries(String keyword, Integer page, InquirySortType sort, Boolean isSolved) {
        Pageable pageable = PageRequest.of(page, INQUIRIES_PER_PAGE, sort.getSort());

        Page<Inquiry> inquiries = inquiryRepository.findInquiries(keyword, isSolved, pageable);

        return inquiries.map(InquirySummaryRes::fromEntity);
    }


    @Transactional(readOnly = true)
    public Page<InquirySummaryRes> getMyInquiries(String keyword, Integer page, InquirySortType sort, Boolean isSolved, Long userId) {
        Pageable pageable = PageRequest.of(page, INQUIRIES_PER_PAGE, sort.getSort());

        Page<Inquiry> myInquiries = inquiryRepository.findMyInquiries(keyword, isSolved, pageable, userId);

        return myInquiries.map(InquirySummaryRes::fromEntity);
    }
}
