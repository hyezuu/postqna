package com.codestates.auth.userdetails;

import com.codestates.auth.utils.CustomAuthorityUtils;
import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class MemberDetailsService implements UserDetailsService {//이름진짜마음에안듬.. 유저디테일즈서비스하고싶어
    private final UserRepository userRepository;
    private final CustomAuthorityUtils authorityUtils;

    public MemberDetailsService(UserRepository userRepository, CustomAuthorityUtils authorityUtils) {
        this.userRepository = userRepository;
        this.authorityUtils = authorityUtils;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(username);
        User findUser = optionalUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        return new MemberDetails(findUser);
        //입력받은 Username(이메일)로 해당유저를 찾아와서 특정필드를가진 Memberdetails객체를 만들어용
    }

    private final class MemberDetails extends User implements UserDetails {
        MemberDetails(User user) {
            setUserId(user.getUserId());
            setEmail(user.getEmail());
            setPassword(user.getPassword());
            setRoles(user.getRoles());
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthoritues(this.getRoles());
        }

        public String getUsername() {
            return getEmail();
        }

        public boolean isAccountNonExpired() {
            return true;
        }

        public boolean isAccountNonLocked() {
            return true;
        }

        public boolean isCredentialsNonExpired() {
            return true;
        }

        public boolean isEnabled() {
            return true;
        }
    }
}
