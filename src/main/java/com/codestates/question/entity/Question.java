package com.codestates.question.entity;

import com.codestates.answer.entity.Answer;
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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccessAuthority accessAuthority = AccessAuthority.PUBLIC;

    @JsonIgnoreProperties("questions")
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @JsonIgnoreProperties("question")//responseDto에서 객체를 필드로 가질때 순환참조 방지
    @OneToOne(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Answer answer;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime modifiedAt = LocalDateTime.now();

    public enum QuestionStatus{

        QUESTION_REGISTERED(1, "질문 등록 상태"),
        QUESTION_ANSWERED(2, "답변 완료 상태"),
        QUESTION_DELETED(3, "질문 삭제 상태"),
        QUESTION_DEACTIVED(4, "질문 비활성화 상태");

        @Getter
        private int statusNumber;
        @Getter
        private String questionStatus;

        QuestionStatus(int statusNumber, String questionStatus) {
            this.statusNumber = statusNumber;
            this.questionStatus = questionStatus;
        }
    }

    public void setUser(User user) {
        this.user = user;
        if(!this.user.getQuestions().contains(this)){
            this.user.getQuestions().add(this);
        }
    }

    public void setAnswer(Answer answer){
        this.answer = answer;
        if(answer.getQuestion() != this){
            answer.setQuestion(this);
        }
    }

}
