package com.codestates.question.mapper;

import com.codestates.question.dto.QuestionDto;
import com.codestates.question.entity.Question;
import com.codestates.user.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    Question questionPatchDtoToQuestion(QuestionDto.Patch requestBody);
    @Mapping(source = "user.userId",target = "userId")
    @Mapping(source = "answer.title",target = "answerTitle")
    @Mapping(source = "answer.content",target = "answerContent")
    QuestionDto.Response questionToQuestionResponse(Question question);

    List<QuestionDto.Response> questionsToQuestionResponses(List<Question> questions);

    default Question questionPostDtoToQuestion(QuestionDto.Post requestBody){
        Question question = new Question();
        User user = new User();
        user.setUserId(requestBody.getUserId());
        question.setUser(user);
        question.setTitle(requestBody.getTitle());
        question.setContent(requestBody.getContent());

        return question;
    }
}
