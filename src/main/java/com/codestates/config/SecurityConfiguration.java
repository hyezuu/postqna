package com.codestates.config;

import com.codestates.auth.filter.JwtAuthenticationFilter;
import com.codestates.auth.filter.JwtCustomUserIdFilter;
import com.codestates.auth.filter.JwtVerificationFilter;
import com.codestates.auth.handler.UserAccessDeniedHandler;
import com.codestates.auth.handler.UserAuthenticationEntryPoint;
import com.codestates.auth.handler.UserAuthenticationFailureHandler;
import com.codestates.auth.handler.UserAuthenticationSuccessHandler;
import com.codestates.auth.jwt.JwtTokenizer;
import com.codestates.auth.utils.CustomAuthorityUtils;
import com.codestates.user.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;


    public SecurityConfiguration(JwtTokenizer jwtTokenizer, CustomAuthorityUtils authorityUtils) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors(withDefaults())
                //세션을 생성하지 않으며, SecurityContext 정보를 얻기 위해 결코 세션을 사용하지 않습니다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new UserAuthenticationEntryPoint())
                .accessDeniedHandler(new UserAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(HttpMethod.POST,"/*/users").permitAll()//회원가입은 모든유저가능
                        .antMatchers(HttpMethod.PATCH,"/*/users/**").hasRole("MEMBER")
                        .antMatchers(HttpMethod.GET,"/*/users").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/users/**").hasAnyRole("MEMBER","ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/users/**").hasRole("MEMBER")
                        .antMatchers(HttpMethod.POST,"/*/questions").hasRole("MEMBER")//질문작성은 유저만
                        .antMatchers(HttpMethod.PATCH,"/*/questions/**").hasRole("MEMBER")
                        .antMatchers(HttpMethod.GET,"/*/questions").hasAnyRole("MEMBER","ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/questions/**").hasAnyRole("MEMBER","ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/questions/**").hasRole("MEMBER")
                        .antMatchers(HttpMethod.POST,"/*/answers").hasRole("ADMIN")//질문작성은 유저만
                        .antMatchers(HttpMethod.PATCH,"/*/answers/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/answers").hasAnyRole("MEMBER","ADMIN")
                        .antMatchers(HttpMethod.GET,"/*/answers/**").hasAnyRole("MEMBER","ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/*/answers/**").hasRole("MEMBER")
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // AbstractHttpConfigurer<T extends AbstractHttpConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B>
    //CustomFilterConfigurer는 AbstractHttpConfigurer상속, HttpSecurity는 HttpSecurityBuilder상속.
    //AbstractHttpConfigurer<T,B> 는 SecurityConfigurerAdapter<DefaultSecurityFilterChain을 상속
    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        public void configure(HttpSecurity builder) throws Exception {
            //getSharedObject(AuthenticationManager.class)를 통해 AuthenticationManager의 객체를 얻을 수 있습니다.
            //동일한 AuthenticationManager 인스턴스를 여러 곳에서 공유할 수 있어 일관성을 유지하고 메모리 사용을 최적화할 수 있음
            //사용자 정의 필터에서 권장되는 방식
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer);
            jwtAuthenticationFilter.setFilterProcessesUrl("/v1/auth/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new UserAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new UserAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);
//            JwtCustomUserIdFilter jwtCustomUserIdFilter = new JwtCustomUserIdFilter(userRepository);
            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
//                    .addFilterAfter(jwtCustomUserIdFilter, JwtVerificationFilter.class);

            //JwtAuthenticationFilter를 Spring Security Filter Chain에 추가 (apply로 연결되어있음)

        }
    }
}

