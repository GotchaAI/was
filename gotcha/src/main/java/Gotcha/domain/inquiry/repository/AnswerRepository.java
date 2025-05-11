package Gotcha.domain.inquiry.repository;

import gotcha_domain.inquiry.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
