package com.codestates.auth.filter;

import com.codestates.auth.jwt.JwtTokenizer;
import com.codestates.auth.utils.CustomAuthorityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    public JwtVerificationFilter(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, Object> claims = verifyJws(request);//인증된 jws임을 확인하고 .claim가져온다
            setAuthenticationToContext(claims);//username과 권한을 authentication 에 담아서 -> 컨텍스트에 저장
        }   catch (SignatureException se) {
            request.setAttribute("exception", se);
        }   catch (ExpiredJwtException ee) {
            request.setAttribute("exception", ee);
        }   catch (Exception e) {
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request, response);
//        문제없이 JWT의 서명 검증에 성공하고, Security Context에 Authentication을 저장한 뒤 다음(Next) Security Filter를 호출
    }

    @Override//값이 없을땐 굳이 필터를 수행하지않아도 됩니다.
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorization = request.getHeader("Authorization");

        return authorization == null || !authorization.startsWith("Bearer");
    }

    private Map<String, Object> verifyJws(HttpServletRequest request) {
        String jws = request.getHeader("Authorization").replace("Bearer ","");
        String base64EncodedSecretKey = jwtTokenizer.encodedBase64SecretKey(jwtTokenizer.getSecretKey());
        Map<String, Object> claims = jwtTokenizer.getClaims(jws, base64EncodedSecretKey).getBody();

        return claims;
    }
    //request에 들어있는 헤더확인할떄마다 모든 과정을 거칠필요가없음!
    private void setAuthenticationToContext(Map<String,Object> claims) {
        String username = (String) claims.get("username");
        List<GrantedAuthority> authorities = authorityUtils.createAuthoritues((List)claims.get("roles"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
        //credential 비워놓는이유 -> password는 민감한정보이기때문에..
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
