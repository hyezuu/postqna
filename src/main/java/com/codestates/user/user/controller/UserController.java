package com.codestates.user.user.controller;

import com.codestates.dto.MultiResponseDto;
import com.codestates.dto.SingleResponseDto;
import com.codestates.user.user.dto.UserDto;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.mapper.UserMapper;
import com.codestates.user.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

//클라이언트에서 requestBody로역직렬화된 데이터 받고,
// responseBody로 직렬화 ->  핸들러 메서드의 리턴 값이 ResponseEntity일 경우,
// 내부적으로 HttpMessageConverter가 동작하게 되어 응답 객체를 직렬화 (이때 jackson에서 getter로 데이터가져옴)
@RestController
@RequestMapping("/v1/users")
@Validated
@Slf4j
public class UserController {

    private final static String USER_DEFAULT_URL = "/v1/users";
    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postUser(@RequestBody @Valid UserDto.Post requestBody) {
        User user = mapper.userPostDtoToUser(requestBody);
        User createdUser = userService.createUser(user);

        URI location = UriComponentsBuilder
                .newInstance()
                .path(USER_DEFAULT_URL + "/{user-id}")
                .buildAndExpand(createdUser.getUserId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{user-id}")
    public ResponseEntity patchUser(
            @PathVariable("user-id") @Positive long userId,
            @RequestBody @Valid UserDto.Patch requestBody) {
        requestBody.setUserId(userId);

        User user =
                userService.updateUser(mapper.userPatchDtoToUser(requestBody));
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.userToUserResponse(user)),
                HttpStatus.OK);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity getUser(
            @PathVariable("user-id") @Positive long userId) {
        User user = userService.findUser(userId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.userToUserResponse(user)),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getUsers(
            @Positive @RequestParam int page,
            @Positive @RequestParam int size) {
        Page<User> pageUsers = userService.findUsers(page - 1, size);
        List<User> users = pageUsers.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.usersToUserResponses(users),
                        pageUsers),
                HttpStatus.OK);
    }

    @DeleteMapping("{user-id}")
    public ResponseEntity deleteUser(
            @PathVariable("user-id") @Positive long userId) {
        userService.deleteUser(userId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
