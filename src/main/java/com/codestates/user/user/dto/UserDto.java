package com.codestates.user.user.dto;

import com.codestates.question.entity.Question;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.entity.UserAuthority;
import com.codestates.validator.NotSpace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    @AllArgsConstructor
    @Getter
    public static class Post {
        @Email(message = "이메일 형식에 맞춰 작성해 주세요 예)hgd@gmail.com")
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        private String email;

        @NotBlank
        private String password;

        @NotBlank(message = "이름은 공백일 수 없습니다.")
        private String name;

        @NotBlank
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 숫자와 '-'으로 구성되어야 합니다.")
        private String phone;
    }

    @Getter
    @Builder
    public static class Patch {
        private Long UserId;

        @NotSpace(message = "회원이름은 공백이 아니어야 합니다.")
        private String name;

        @NotSpace
        @Pattern(regexp = "^010-\\d{4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phone;

        private User.UserStatus userStatus;

        public void setUserId(Long userId) {
            UserId = userId;
        }
    }

    @Getter
    @Builder
    public static class Response {
        private long userId;
        private String email;
        private String name;
        private String phone;
//        private UserAuthority userAuthority;
        private User.UserStatus userStatus;
        private List<Question> questions = new ArrayList<>();
        private String weather;

//        public String getUserAuthority() {
//            return userAuthority.getAuthority();
//        }

        public String getUserStatus() {
            return userStatus.getStauts();
        }

        public List<String> getQuestions() {
            List<String> questionList = questions.stream()
                    .map(question ->
                            question.getQuestionId() + "." + (question.getTitle()))
                    .collect(Collectors.toList());
            if (questionList.isEmpty()) {
                return List.of("작성한 글이 없습니다.");
            } else {
                return questionList;
            }
        }

    }
}



