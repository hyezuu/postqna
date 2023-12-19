package com.codestates.question.repository;

import com.codestates.question.entity.Question;
import com.codestates.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository <Question,Long> {
    List<Question> findByUser_UserId(Long userId);
    //두개의 Entity중 Question에서 어떠한 User객체와 연결된 정보를 모두 갖고 오고싶을때
    //findBy+(fk를 관리하는 entity의 필드명에서 첫글자 대문자)+_+(fk를 관리하는 entity의 식별자(PK)필드명에서 첫글자 대문자)

}
