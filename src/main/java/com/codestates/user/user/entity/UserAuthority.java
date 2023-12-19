package com.codestates.user.user.entity;

import lombok.Getter;
import lombok.Setter;

public enum UserAuthority {
    Admin("관리자"),
    Member("회원");

    @Getter
    private final String authority;

    UserAuthority(String authority) {
        this.authority = authority;
    }
}