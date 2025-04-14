package Gotcha.domain.inquiry.service;

import Gotcha.common.exception.CustomException;
import Gotcha.domain.inquiry.dto.AnswerReq;
import Gotcha.domain.inquiry.entity.Answer;
import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.inquiry.exception.InquiryExceptionCode;
import Gotcha.domain.inquiry.repository.AnswerRepository;
import Gotcha.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    private final InquiryService inquiryService;

    @Transactional
    public void createAnswer(AnswerReq answerReq, Long inquiryId, Long userId) {
        User writer = inquiryService.getValidUser(userId);
        Inquiry inquiry = inquiryService.getValidInquiry(inquiryId);
        if(inquiry.getIsSolved())
            throw new CustomException(InquiryExceptionCode.ALREADY_SOLVED);
        Answer answer = answerReq.toEntity(writer);
        inquiry.solve(answer);
        answerRepository.save(answer);
    }

}
