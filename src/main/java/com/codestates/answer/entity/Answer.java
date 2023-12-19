package com.codestates.answer.entity;

import com.codestates.question.entity.AccessAuthority;
import com.codestates.question.entity.Question;
import com.codestates.user.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @JsonIgnoreProperties("answer")
    @OneToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @JsonIgnoreProperties("answers")
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(nullable = false)
    private String title;//질문제목 에 대한답변입니다.

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessAuthority accessAuthority;//public이면 - 모든사람가능, private이면 - 작성자와 관리자만 가능

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public void setQuestion(Question question) {
        this.question = question;
        if(question.getAnswer() != this){
            question.setAnswer(this);
        }
    }
    public void setUser(User user) {
        this.user = user;
        if(!user.getAnswers().contains(this)){
            user.getAnswers().add(this);
        }
    }
}
