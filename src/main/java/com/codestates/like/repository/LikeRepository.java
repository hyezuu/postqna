package com.codestates.like.repository;

import com.codestates.answer.entity.Answer;
import com.codestates.like.entity.Like;
import com.codestates.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndAnswer(User user, Answer answer);
    void deleteByUserAndAnswer(User user, Answer answer);
}
