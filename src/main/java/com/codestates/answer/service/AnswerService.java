package com.codestates.answer.service;

import com.codestates.answer.entity.Answer;
import com.codestates.answer.repository.AnswerRepository;
import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import com.codestates.question.entity.Question;
import com.codestates.question.service.QuestionService;
import com.codestates.user.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;

    private final UserService userService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService, UserService userService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
        this.userService = userService;
    }

    //공통확인사항 -> 질문의 상태를 확인하고 답변/수정/삭제 수행
    public Answer createAnswer(Answer answer) {
        //메서드 추출할지말지 고민이 됩니다..
        verifyQuestion(answer);
        verifyUser(answer);//존재하는유저인지
//        verifyAuthority(answer);//유저권한확인 -> 시큐리티로 확인

        Question findquestion = questionService.findVerifiedQuestion(answer.getQuestion().getQuestionId());
        answer.setTitle("질문에 대한 답변입니다 : " + findquestion.getTitle());

        Answer savedAnswer = saveAnswer(answer);//답변등록

        findquestion.setQuestionStatus(Question.QuestionStatus.QUESTION_ANSWERED);
        questionService.modifyQuestion(findquestion); //질문상태변경

        return savedAnswer;
    }

    public Answer updateAnswer(Answer answer) {
        Answer findAnswer = findVerifyAnswer(answer.getAnswerId());
        //답변등록한관리자인지 확인해야함(Security로 구현)

        Optional.ofNullable(answer.getContent())
                .ifPresent(findAnswer::setContent);

        findAnswer.setModifiedAt(LocalDateTime.now());

        return saveAnswer(findAnswer);
    }

    @Transactional(readOnly = true)
    public Answer findanswer(long answerId) {
        return findVerifyAnswer(answerId);//접근권한..질문상태따라 달라야함(삭제된질문인지)
    }

    @Transactional(readOnly = true)
    public Page<Answer> findAnswers(int page, int size) {
        return answerRepository.findAll(
                PageRequest.of(page, size, Sort.by("questionId").descending())
        );
    }

    public void deleteAnswer(long answerId) {
        Answer answer = findVerifyAnswer(answerId);
        answerRepository.delete(answer);
    }


    private void verifyQuestion(Answer answer) {
        questionService.findVerifiedQuestion(answer.getQuestion().getQuestionId());
    }

    private void verifyUser(Answer answer) {
        userService.findVerifiedUser(answer.getUser().getUserId());
    }

//    private void verifyAuthority(Answer answer) {
//        if (answer.getUser().getUserAuthority().equals("MEMBER")) {
//            throw new RuntimeException("답변은 관리자만 작성할 수 있습니다.");
//        }
//    }

    private Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    private Answer findVerifyAnswer(long answerId) {
        Answer findAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND));
        return findAnswer;
    }

}
