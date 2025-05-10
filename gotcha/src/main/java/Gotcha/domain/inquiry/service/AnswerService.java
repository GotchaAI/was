package Gotcha.domain.inquiry.service;

import Gotcha.domain.inquiry.dto.AnswerReq;
import Gotcha.domain.inquiry.entity.Answer;
import Gotcha.domain.inquiry.entity.Inquiry;
import Gotcha.domain.inquiry.repository.AnswerRepository;
import Gotcha.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    @Transactional
    public void createAnswer(AnswerReq answerReq, Inquiry inquiry, User writer) {
        Answer answer = answerReq.toEntity(writer);
        inquiry.solve(answer);
        answerRepository.save(answer);
    }
}