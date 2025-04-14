package Gotcha.domain.inquiry.service;

import Gotcha.common.exception.CustomException;
import Gotcha.domain.inquiry.dto.InquiryReq;
import Gotcha.domain.inquiry.dto.InquiryRes;
import Gotcha.domain.inquiry.dto.InquirySortType;
import Gotcha.domain.inquiry.dto.InquirySummaryRes;
import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.inquiry.exception.InquiryExceptionCode;
import Gotcha.domain.inquiry.repository.InquiryRepository;
import Gotcha.domain.user.entity.User;
import Gotcha.domain.user.exceptionCode.UserExceptionCode;
import Gotcha.domain.user.repository.UserRepository;
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

    private final UserRepository userRepository;

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

    @Transactional(readOnly = true)
    public InquiryRes getInquiryById(Long inquiryId) {
        Inquiry inquiry = getValidInquiry(inquiryId);

        return InquiryRes.fromEntity(inquiry);
    }

    @Transactional
    public void createInquiry(InquiryReq inquiryReq, Long userId) {
        User writer = getValidUser(userId);
        Inquiry inquiry = inquiryReq.toEntity(writer);
        inquiryRepository.save(inquiry);
    }

    private User getValidUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));
    }

    @Transactional
    public void updateInquiry(InquiryReq inquiryReq, Long inquiryId, Long userId) {
        Inquiry inquiry = getValidInquiry(inquiryId);
        validateInquiryOwner(inquiry, userId);
        inquiry.update(inquiryReq.title(), inquiryReq.content(), inquiryReq.isPrivate());
    }

    private Inquiry getValidInquiry(Long inquiryId){
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(InquiryExceptionCode.INVALID_INQUIRYID))
    }

    private void validateInquiryOwner(Inquiry inquiry, Long userId){
        if (!inquiry.getWriter().getId().equals(userId)) {
            throw new CustomException(InquiryExceptionCode.UNAUTHORIZED_ACTION);
        }
    }

}
