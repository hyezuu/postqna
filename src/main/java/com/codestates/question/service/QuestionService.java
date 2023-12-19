package com.codestates.question.service;

import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import com.codestates.question.entity.Question;
import com.codestates.question.repository.QuestionRepository;
import com.codestates.user.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;

    public QuestionService(QuestionRepository questionRepository, UserService userService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
    }

    public Question createQuestion (Question question){
        //유효한 회원인지 확인
        userService.findVerifiedUser(question.getUser().getUserId());
        //질문 등록 유저의 권한이 관리자면 -> 질문은 회원만 작성할 수 있습니다.
//        if(question.getUser().getUserAuthority().equals(UserAuthority.Admin)) {
//            throw new RuntimeException("질문은 일반 회원만 작성할 수 있습니다.");
//        }
//        Question savedQuestion = saveQuestion(question);
//        savedQuestion.setAnswer(new Answer());
        return saveQuestion(question);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    //헤더에포함된 멤버정보랑, question의 멤버정보가 같은지 확인.....하고싶은데
    public Question modifyQuestion (Question question){
        //멤버아이디받아서 게시물 멤버아이디랑 같으ㄴ지확인(시큐리티로확인)
    Question findQuestion = findVerifiedQuestion(question.getQuestionId());

    Optional.ofNullable(question.getTitle())
            .ifPresent(findQuestion::setTitle);
    Optional.ofNullable(question.getContent())
            .ifPresent(findQuestion::setContent);

    Optional.ofNullable(question.getAccessAuthority())
            .ifPresent( accessAuthority -> {
                findQuestion.setAccessAuthority(question.getAccessAuthority());
                Optional.ofNullable(findQuestion.getAnswer())
                        .ifPresent(answer -> findQuestion.getAnswer().setAccessAuthority(question.getAccessAuthority()));
            }//엔티티리스너 사용 ,,?
    );

    findQuestion.setModifiedAt(LocalDateTime.now());
        return saveQuestion(findQuestion);
    }

    @Transactional(readOnly = true)
    public Question findQuestion(long questionId){
        return findVerifiedQuestion(questionId);
    }

    @Transactional(readOnly = true)
    public Page<Question> findQuestionsByUser(long userId, int page, int size){
        List<Question> questions = questionRepository.findByUser_UserId(userId)
                .stream().filter(question -> question.getQuestionStatus().getStatusNumber() <= 2)
                .collect(Collectors.toList());
        Page<Question> pageQuestions = new PageImpl<>(questions,
                PageRequest.of(page,size, Sort.by("questionId").descending()),questions.size());
        return pageQuestions;
    }
    @Transactional(readOnly = true)
    public Page<Question> findQuestions (int page, int size){
        List<Question> questions = questionRepository.findAll()
                .stream().filter(question -> question.getQuestionStatus().getStatusNumber() <= 2)
                .collect(Collectors.toList());
        Page<Question> pageQuestions = new PageImpl<>(questions,
                PageRequest.of(page,size, Sort.by("questionId").descending()),questions.size());
        return pageQuestions;
    }

    public Question deleteQuestion(long questionId){
        Question findQuestion = findVerifiedQuestion(questionId);
        findQuestion.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED);
        return saveQuestion(findQuestion);
    }

    public Question findVerifiedQuestion(long questionId) {
        Question findQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        return findQuestion;
    }
}
