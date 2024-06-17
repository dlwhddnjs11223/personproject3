package com.sparta.newspeed.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newspeed.dto.UserServiceReqDto;
import com.sparta.newspeed.entity.UserStatusEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;


    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            UserServiceReqDto UserServiceReqDto = new ObjectMapper().readValue(request.getInputStream(), UserServiceReqDto.class); // httpServletRequest의 request를 LoginRequestDto로 변환


            return getAuthenticationManager().authenticate( // 인증객체 만들기.
                    new UsernamePasswordAuthenticationToken(
                            UserServiceReqDto.getEmail(),
                            UserServiceReqDto.getPassword(),
                            null //토큰을 만들어서 메서드 호출하고 ㅇ씨는 형태
                            //authenticate를 활용하면
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // 콜백메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getEmail();
        UserStatusEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserStatus();
//
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken();

        jwtUtil.addAccessJwtToHeader(accessToken, response);
        jwtUtil.addRefreshJwtToHeader(refreshToken, response);

        // 1. 메서드 종료도미ㅕㄴ dofilter가 없기때문에 이대로 클라이언트한테 반환?
        // 2. 다음 필터로 이동 시킨다 하더라도 이 요청에 대해서는 인증이 필요 없다고 허가를 내려놓은 상태.
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        if (uri.startsWith("/api/user")) {
            chain.doFilter(request, response);
            return;
        }
        super.doFilter(request, response, chain);

    }
}

