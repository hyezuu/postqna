package com.codestates.user.user.service;

import com.codestates.auth.utils.CustomAuthorityUtils;
import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import com.codestates.question.entity.Question;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.entity.UserAuthority;
import com.codestates.user.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CustomAuthorityUtils authorityUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    public User createUser(User user) {

//        veryfyAdminEmail(user);//관리자 이메일인지 -> 맞으면 권한등록

        findRegisteredUser(user);//등록된 유저인지?

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        List<String> roles = authorityUtils.createRoles(user.getEmail());
        user.setRoles(roles);

        User savedUser = saveUser(user);
        savedUser.setQuestion(new Question());

        return savedUser;
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {//ㅅ수정도 본인만 가능해야댐
        //중복된메서드 -> , 비밀번호 변경 가능하도록 (patch수정할때할것)
        User findUser = findVerifiedUser(user.getUserId());

        Optional.ofNullable(user.getName())
                .ifPresent(findUser::setName);
        Optional.ofNullable(user.getPhone())
                .ifPresent(findUser::setPhone);
        Optional.ofNullable(user.getUserStatus())
                .ifPresent(findUser::setUserStatus);

        return saveUser(findUser);
    }

    @Transactional(readOnly = true)
    public User findUser(long userId) {
        //아이디로유저찾기
        return findVerifiedUser(userId);
    }
    @Transactional(readOnly = true)
    public Page<User> findUsers(int page, int size) {
        //페이지네이션
        return userRepository.findAll
                (PageRequest.of(page, size, Sort.by("userId").descending()));
    }

    public void deleteUser(long userId) {
        //아이디로유저찾아서삭함 -> 삭제 ? 상태변경 ?
        userRepository.delete(findVerifiedUser(userId));
    }

    public void findRegisteredUser(User user) {
        Optional<User> findEmail = userRepository.findByEmail(user.getEmail());
//        if (findEmail.isPresent()) {
//            throw new RuntimeException("이미 등록된 이메일입니다.");
//        }
        findEmail.ifPresent( u -> {
            throw new BusinessLogicException(ExceptionCode.USER_EXISTS);
        }); //ifPresent를 사용한 방법
        Optional<User> findPhone = userRepository.findByPhone(user.getPhone());
        if (findPhone.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.USER_EXISTS);
        }
    }

    public User findVerifiedUser(long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        User user = findUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        return user;
    }

//    public void veryfyAdminEmail(User user) {
//        if (user.getEmail().equals("admin@gmail.com")) {
//            user.setUserAuthority(UserAuthority.Admin);//예외처리
//        }
//    }


}
