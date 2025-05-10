package Gotcha.domain.inquiry.repository;

import Gotcha.domain.inquiry.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
