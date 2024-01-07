package com.codestates.like.service;

import com.codestates.answer.entity.Answer;
import com.codestates.answer.service.AnswerService;
import com.codestates.like.entity.Like;
import com.codestates.like.repository.LikeRepository;
import com.codestates.user.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {
    private final AnswerService answerService;
    private final LikeRepository likeRepository;

    public LikeService(AnswerService answerService, LikeRepository likeRepository) {
        this.answerService = answerService;
        this.likeRepository = likeRepository;
    }

    public void addLike(long answerId, User user){
        Answer answer = answerService.findanswer(answerId);
        if(!likeRepository.existsByUserAndAnswer(user, answer)){//member가 comment에 좋아요를 누른적 없는경우
            answer.setLikeCount(answer.getLikeCount()+1);
            likeRepository.save(new Like(user, answer));
        }
        else{
            answer.setLikeCount(answer.getLikeCount()-1);
            likeRepository.deleteByUserAndAnswer(user, answer);//member가 comment에 좋아요를 누른 경우
        }
    }
}
