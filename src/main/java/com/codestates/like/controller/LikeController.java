package com.codestates.like.controller;

import com.codestates.like.service.LikeService;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/likes")
public class LikeController {
    private final LikeService likeService;
    private final UserService userService;

    public LikeController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }
    @PostMapping("/answer/{answer-id}")
    public ResponseEntity addLikeToAnswer(@PathVariable("answer-id")@Positive long answerId,
                                  @RequestParam @Positive long userId){
        User user = userService.findUser(userId);//member Repository or memberService에서 member가져오는 로직 필요

        likeService.addLike(answerId,user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
