package com.codestates.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthorityUtils {
    @Value("${mail.address.admin}")
    private String adminMailAddress;

    private final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN");//필요시 유저권한추가
    private final List<GrantedAuthority> MEMBER_ROLES = AuthorityUtils.createAuthorityList("ROLE_MEMBER");

    private final List<String> ADMIN_ROLES_STRING = List.of("ADMIN");//db저장용
    private final List<String> MEMBER_ROLES_STRING = List.of("MEMBER");

    //메모리 상의 ROLE 기반으로 권한정보 생성 (회원가입시 권한정보생성)
    public List<GrantedAuthority> createAuthorities(String email) {
        if (email.equals(adminMailAddress)) {
            return ADMIN_ROLES;
        }
        return MEMBER_ROLES;
    }

    //// DB에 저장된 Role을 기반으로 권한 정보 생성 (로그인할때
    //데이버에스에서 인증 후에, 가진 권한으로 인가를 위해 권한정보를 설정하는 코드)
    public List<GrantedAuthority> createAuthoritues(List<String> roles) {//db의 의 role을 가져온다 (ADMIN,MEMBER)
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))//각각에 ROLE_ADMIN -> 과같은형태로 바꿔서 권한을 생성함
                .collect(Collectors.toList());
        return authorities;
    }

    //회원가입시 db저장용
    public List<String> createRoles(String email) {
        if (email.equals(adminMailAddress)) {
            return ADMIN_ROLES_STRING;
        }
        return MEMBER_ROLES_STRING;
    }

}
