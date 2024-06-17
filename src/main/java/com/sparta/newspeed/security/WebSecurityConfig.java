package com.sparta.newspeed.security;

import com.sparta.newspeed.service.RefreshTokenService;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {


    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationConfiguration authenticationConfiguration;


    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshTokenService = refreshTokenService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { //
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter() {
        return new ExceptionHandlerFilter();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
//        filter.setRequiresAuthenticationRequestMatcher(new NegatedRequestMatcher(new AntPathRequestMatcher("/api/user/**"))); // TIL 작성
        return filter;

    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, refreshTokenService, userDetailsService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
//                                .requestMatchers("/user/**").permitAll()
                                .requestMatchers("/user/login").permitAll()
                                .requestMatchers("/user/signup").permitAll() // 인증이 필요 없다, 토큰이 없어도 된다.
                                .requestMatchers(HttpMethod.GET, "/peeds/**").permitAll()// '/api/user/'로 시작하는 요청 모두 접근 허가
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()

                // 그 외 모든 요청 인증처리
        );


//         필터 관리


        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); // 인가필터
        http.addFilterBefore(exceptionHandlerFilter(), JwtAuthorizationFilter.class); // 예외 핸들링 필터
        http.addFilterBefore(characterEncodingFilter, ExceptionHandlerFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // 인증 필터

        // 예외 > 인가 > 인증


        return http.build();
    }


}