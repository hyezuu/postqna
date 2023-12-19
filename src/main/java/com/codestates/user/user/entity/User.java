package com.codestates.user.user.entity;

import com.codestates.answer.entity.Answer;
import com.codestates.question.entity.Question;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "USERS")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)//인코딩시 값이 늘어날 수 있음을 대비..?
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 13, unique = true)
    private String phone;

//    @Column(nullable = false, updatable = false)//잘가라....시큐리티에 넌 필요없어
//    @Enumerated(EnumType.STRING)
//    private UserAuthority userAuthority = UserAuthority.Member;

    @ElementCollection(fetch = FetchType.EAGER)//시큐리티를위한....
    private List<String> roles = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.USER_ACTIVE;

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user")
    private List<Question> questions = new ArrayList<>();

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user")
    private List<Answer> answers = new ArrayList<>();

    public enum UserStatus {

        USER_ACTIVE("활동중"),
        USER_SLEEP("휴면 상태"),
        USER_QUIT("탈퇴 상태");

        @Getter
        private String stauts;

        UserStatus(String stauts) {
            this.stauts = stauts;
        }
    }

    public void setQuestion(Question question){
        questions.add(question);
        if(question.getUser() != this){
            question.setUser(this);
        }
    }

    public void setAnswer(Answer answer){
        answers.add(answer);
        if(answer.getUser() != this){
            answer.setUser(this);
        }
    }
    
}
