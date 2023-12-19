package com.codestates.question.entity;


import lombok.Getter;
import lombok.Setter;


public enum AccessAuthority {
    PUBLIC("공개글 상태"),
    PRIVATE("비밀글 상태");

    @Getter
    private String authority;

    AccessAuthority(String authority) {
        this.authority = authority;
    }
}
