package com.sparta.newspeed.security;

import com.sparta.newspeed.service.LogoutAccessTokenService;
import com.sparta.newspeed.entity.UserStatusEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // JWT 데이터
    private final LogoutAccessTokenService logoutAccessTokenService;
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Header KEY 값
    public static final String REFRESH_HEADER = "Refresh";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // Token 식별자
    public static final String REFRESH = "Rfresh ";
    // 토큰 만료시간
    private final long ACCESSTOKEN_TIME = 60 * 30 * 1000L; // 60분

    // 토큰 만료시간
    private final long REFRESHTOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; // 600분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct // 딱 한번만 받아오면 되는 값을 사용할때마다 요청을 새로 호출하는 실수를 방지하기 위해 사용
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }
    // JWT 생성

    public String createAccessToken(String email, UserStatusEnum status) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, status) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + ACCESSTOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }


    public String createRefreshToken() {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setExpiration(new Date(date.getTime() + REFRESHTOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();

    }

    public void addAccessJwtToHeader(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            res.setHeader(AUTHORIZATION_HEADER, token);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }


    public void addRefreshJwtToHeader(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            res.setHeader(REFRESH_HEADER, token);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    // Cookie에 들어있던 JWT 토큰을 Substring

    public String substringAccessToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("JwtUtil 118 : 액세스 토큰이 유효하지 않습니다.");
    }

    public String substringRefreshToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("JwtUtil 126 : 리프레쉬 토큰이 유효하지 않습니다.");
    }




    // JWT 검증

    public boolean validateAccessToken(String token) {
        try {
            if(logoutAccessTokenService.isExistLogoutToken(token)){
                throw new IllegalArgumentException("JwtUtil 137 :로그아웃 처리된 액세스 토큰입니다, 다시 로그인 해주세요");
            }
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.error("액세스 토큰 : Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        }

        return false;

    }

    public boolean validateRefreshToken(String token) {
        try {

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException |
                 UnsupportedJwtException | IllegalArgumentException e) {

            log.error("리프레쉬 토큰 : Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("JwtUtil 159 : 리프레쉬 토큰이 만료 됐습니다. 재로그인 해주세요.");

        }

        return false;

    }


    // JWT에서 사용자 정보 가져오기

    public Claims getUserInfoFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }

    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest req) {

        String header = req.getHeader("Authorization");


        if (StringUtils.isEmpty(header)) {

            return null;
        }
        try {
            return URLDecoder.decode(header, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String getRefreshTokenFromHeader(HttpServletRequest req) {
        String header = req.getHeader("Refresh");


        if (StringUtils.isEmpty(header)) {

            return null;
        }
        try {
            return URLDecoder.decode(header, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}