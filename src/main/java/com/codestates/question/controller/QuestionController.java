package com.codestates.question.controller;

import com.codestates.dto.MultiResponseDto;
import com.codestates.dto.SingleResponseDto;
import com.codestates.question.dto.QuestionDto;
import com.codestates.question.entity.Question;
import com.codestates.question.mapper.QuestionMapper;
import com.codestates.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
//@RequestMapping("/v1/questions")
@Validated
@Slf4j
public class QuestionController {
    private final static String QUESTION_DEFAULT_URL = "/v1/questions";
    private final QuestionService questionService;
    private final QuestionMapper mapper;

    public QuestionController(QuestionService questionService, QuestionMapper mapper) {
        this.questionService = questionService;
        this.mapper = mapper;
    }

    //    @PostMapping
    @RequestMapping(value = "/v1/questions", method = RequestMethod.POST)
    public ResponseEntity postQuestion(@RequestBody @Valid QuestionDto.Post requestBody) {
        Question question = mapper.questionPostDtoToQuestion(requestBody);
        Question createdQuestion = questionService.createQuestion(question);

        URI location = UriComponentsBuilder
                .newInstance()
                .path(QUESTION_DEFAULT_URL + "/{question-id}")
                .buildAndExpand(createdQuestion.getQuestionId())
                .toUri();//리팩토링때 빼기
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/v1/questions/{question-id}", method = RequestMethod.PATCH)
//    @PatchMapping("/{question-id}")
    public ResponseEntity patchQuestion(
            @PathVariable("question-id") @Positive long questionId,
            @RequestBody @Valid QuestionDto.Patch requestBody) {
        requestBody.setQuestionId(questionId);

        Question question =
                questionService.modifyQuestion(mapper.questionPatchDtoToQuestion(requestBody));
        return new ResponseEntity(
                new SingleResponseDto<>(mapper.questionToQuestionResponse(question)),
                HttpStatus.OK);
    }

    //    @GetMapping("/{question-id}")
    @RequestMapping(value = "/v1/questions/{question-id}", method = RequestMethod.GET)
    public ResponseEntity getQuestion(
            @PathVariable("question-id") @Positive long questionId) {
        Question question = questionService.findQuestion(questionId);
        return new ResponseEntity(
                new SingleResponseDto<>(
                        mapper.questionToQuestionResponse(question)),
                HttpStatus.OK);
    }

    //    @GetMapping
    @RequestMapping(value = "/v1/questions", method = RequestMethod.GET, params = {"userId"})
    public ResponseEntity getQuestions(
            @Positive @RequestParam long userId,
            @Positive @RequestParam int page,
            @Positive @RequestParam int size) {

        Page<Question> pageQuestions = questionService.findQuestionsByUser(userId, page - 1, size);
        List<Question> questions = pageQuestions.getContent();
        return new ResponseEntity(
                new MultiResponseDto<>(mapper.questionsToQuestionResponses(questions),
                        pageQuestions),
                HttpStatus.OK);
    }

    //    @GetMapping
    @RequestMapping(value = "/v1/questions", method = RequestMethod.GET)
    public ResponseEntity getQuestions(
            @Positive @RequestParam int page,
            @Positive @RequestParam int size) {
        Page<Question> pageQuestions = questionService.findQuestions(page - 1, size);
        List<Question> questions = pageQuestions.getContent();
        return new ResponseEntity(
                new MultiResponseDto<>(mapper.questionsToQuestionResponses(questions),
                        pageQuestions),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/questions/{question-id}", method = RequestMethod.DELETE)
//    @DeleteMapping("/{question-id}")
    public ResponseEntity deleteQuestion(
            @PathVariable("question-id") @Positive long questionId) {
        questionService.deleteQuestion(questionId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


}
