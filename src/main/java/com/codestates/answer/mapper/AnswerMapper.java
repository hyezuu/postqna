package com.codestates.answer.mapper;

import com.codestates.answer.dto.AnswerDto;
import com.codestates.answer.entity.Answer;
import com.codestates.question.entity.Question;
import com.codestates.question.mapper.QuestionMapper;
import com.codestates.user.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    default Answer answerPostDtoToAnswer(AnswerDto.Post requestBody){
        Answer answer = new Answer();
        Question question = new Question();
        User user = new User();

        question.setQuestionId(requestBody.getQuestionId());
        user.setUserId(requestBody.getUserId());
        answer.setUser(user);
        answer.setQuestion(question);
//        answer.setTitle(answer.getQuestion().getTitle() + "에 대한 답변입니다.");
        answer.setAccessAuthority(question.getAccessAuthority());
        answer.setContent(requestBody.getContent());
        return answer;
    }
    Answer answerPatchDtoToAnswer(AnswerDto.Patch requestBody);

    @Mapping(target = "questionTitle", source = "question.title")
    @Mapping(target = "questioner", source = "question.user.name")
    @Mapping(target = "answerer", source = "user.name")
    AnswerDto.Response answerToAnswerResponseDto(Answer answer);

    List<AnswerDto.Response> answersToAnswerResponseDtos(List<Answer> answers);

}
