package com.sparta.newspeed.security;

import com.sparta.newspeed.entity.RefreshToken;
import com.sparta.newspeed.service.RefreshTokenService;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {


        //req에서 jwt를 찾고
        String accesstoken = jwtUtil.getAccessTokenFromHeader(req);


        // 찾은 jwt를 substring
        if (StringUtils.hasText(accesstoken)) {
            // JWT 토큰 substring
            String accessTokenValue = jwtUtil.substringAccessToken(accesstoken);



            if (!jwtUtil.validateAccessToken(accessTokenValue)) { // 액세스 토큰이 유효하지 않으면

                log.error("액세스 토큰 만료");

                //
                String refreshToken = jwtUtil.getRefreshTokenFromHeader(req);


                if (StringUtils.hasText(refreshToken)) {
                    // JWT 토큰 substring
                    String RefreshTokenValue = jwtUtil.substringRefreshToken(refreshToken);

                    if (!jwtUtil.validateRefreshToken(RefreshTokenValue)) {
                        throw new IllegalArgumentException
                                ("JWTAuthorizationFIlter 64 : 유효하지 않은 리프레쉬 토큰입니다. 다시 로그인 하세요.");
                    }

                    RefreshToken issuedRefreshToken = refreshTokenService.findByRefreshToken(RefreshTokenValue);
                    if(issuedRefreshToken.isExpired()){
                        throw new IllegalArgumentException
                                ("JwtAuthorizationFilter 69 : 로그아웃 처리된 리프레쉬 토큰입니다. 다시 로그인 해주세요.");
                    }
                    log.info("DB 기존 리프레쉬 토큰" + issuedRefreshToken.getRefreshToken());
                    User user = issuedRefreshToken.getUser();
                    String email = user.getEmail();
                    UserStatusEnum status = user.getUserStatus();

                    accesstoken = jwtUtil.createAccessToken(email, status);
                    refreshToken = refreshTokenService.updateRefreshToken(issuedRefreshToken);
                    log.info("DB 새로운 리프레쉬 토큰" + issuedRefreshToken.getRefreshToken());

                    jwtUtil.addAccessJwtToHeader(accesstoken, res);
                    jwtUtil.addRefreshJwtToHeader(refreshToken, res);
//
                    String newAccesstokenValue = jwtUtil.substringAccessToken(accesstoken);
                    log.info("새로운 액세스 토큰:" + newAccesstokenValue);
                    Claims info = jwtUtil.getUserInfoFromToken(newAccesstokenValue);

                    try {
                        setAuthentication(info.getSubject());
                        // info에 담긴 subjedct(name)로 인증객체 생성 및 인증처리
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        return;
                    }
                    //
                } else {
                    throw new IllegalArgumentException("JWTAuthorizationFIlter 97 : 리프레쉬 토큰이 없습니다.");
                }
                filterChain.doFilter(req, res);
            }//


            Claims info = jwtUtil.getUserInfoFromToken(accessTokenValue);

            try {
                setAuthentication(info.getSubject());
                // info에 담긴 subjedct(name)로 인증객체 생성 및 인증처리
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }
        filterChain.doFilter(req, res);
    }


    //  인증 처리 = security context holder 생성해주는 것
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }


    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}