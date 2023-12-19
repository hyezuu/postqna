package com.codestates.question.dto;

import com.codestates.answer.entity.Answer;
import com.codestates.question.entity.AccessAuthority;
import com.codestates.question.entity.Question;
import com.codestates.validator.NotSpace;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class QuestionDto {
    @Getter
    public static class Post {
        @Positive
        private long userId;

        @NotBlank(message = "제목을 입력해주세요.")
        private String title;

        @NotBlank(message = "내용을 입력해주세요.")
        private String content;

//        @NotSpace//공개설정 여부x -> 기본값..(공개)
        private AccessAuthority accessAuthority = AccessAuthority.PUBLIC;

//        {
//            "userId" : 1,
//                "title" : "sdsf",
//                "content" : "ss"
//        }

    }
    @Getter
    public static class Patch {
        @Positive
        private long questionId;



        @NotSpace(message = "제목을 입력해주세요.")
        private String title;

        @NotSpace(message = "내용을 입력해주세요.")
        private String content;

        private AccessAuthority accessAuthority;

        public void setQuestionId(long questionId) {
            this.questionId = questionId;
        }
    }
    @Builder
    @Getter
    public static class Response {
        private long questionId;
        private long userId;
        private String title;
        private String content;
        private String answerTitle;
        private String answerContent;
        private Question.QuestionStatus questionStatus;
        private AccessAuthority accessAuthority;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
