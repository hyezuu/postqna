package com.codestates.answer.dto;

import com.codestates.answer.entity.Answer;
import com.codestates.question.dto.QuestionDto;
import com.codestates.question.entity.AccessAuthority;
import com.codestates.question.entity.Question;
import com.codestates.user.user.dto.UserDto;
import com.codestates.user.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public class AnswerDto {
    @Getter
    public static class Post {
        @Positive
        private long questionId;

        @Positive
        private long userId;

        @NotBlank(message = "내용을 입력해주세요.")
        private String content;

    }

    @Getter
    public static class Patch {
        @Positive
        private long answerId;

        @NotBlank(message = "내용을 입력해주세요.")
        private String content;

        public void setAnswerId(long answerId) {
            this.answerId = answerId;
        }
    }

    @Builder
    @Getter
    public static class Response {
        private String questionTitle;
        private String questioner;
        private String answerer;
        private String title;
        private String content;
        private AccessAuthority accessAuthority;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

}
