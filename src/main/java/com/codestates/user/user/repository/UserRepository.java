package com.codestates.user.user.repository;

import com.codestates.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);//등록된 이메일인지
    Optional<User> findByPhone(String phone);//등록된 휴대폰번호인지
 }
