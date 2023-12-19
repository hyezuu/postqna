package com.codestates.auth.filter;

import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import com.codestates.user.user.entity.User;
import com.codestates.user.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtCustomUserIdFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public JwtCustomUserIdFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 현재 사용자의 userId..
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User findUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        String userId = Long.toString(findUser.getUserId());

        // 사용자가 요청한 userId 가져오기
        String requestedUserId = extractUserIdFromRequest(request);

        // 현재 사용자의 username과 요청된 userId가 일치하는지 확인
        if (userId.equals(requestedUserId)||requestedUserId==null) {
            // userId가 일치하는 경우에만 다음 필터로 이동
            chain.doFilter(request, response);
        } else {//ㅇㅏ니면 예외를 던져서 공통으로 처리할수있게하는ㄴ게낫나?
//            response.getWriter().write("Access Denied. User does not have permission for this resource.");
            response.setStatus(403);
        }
    }

    private String extractUserIdFromRequest(ServletRequest request) {
        return request.getParameter("user-id");
    }
}
