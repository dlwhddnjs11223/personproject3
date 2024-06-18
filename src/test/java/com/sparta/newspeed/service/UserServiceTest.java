package com.sparta.newspeed.service;

import com.sparta.newspeed.dto.SignupReqDto;
import com.sparta.newspeed.dto.UserServiceReqDto;
import com.sparta.newspeed.dto.WithdrawReqDto;
import com.sparta.newspeed.email.EmailService;
import com.sparta.newspeed.entity.RefreshToken;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import com.sparta.newspeed.repository.UserRepository;
import com.sparta.newspeed.security.JwtUtil;
import com.sparta.newspeed.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private LogoutAccessTokenService LogoutAccessTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtil jwtUtil;


    UserService userService;

    @BeforeEach
    void setUp() {

        userService = new UserService(
                passwordEncoder,
                userRepository,
                refreshTokenService,
                LogoutAccessTokenService,
                emailService,
                jwtUtil);
    }

    @Test
    @DisplayName("회원가입")
    void test1() {
        //given
        String nickname = "test";
        String password = "test";
        String username = "test";
        String email = "test@test.com";
        String introduce = "this is test";

        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);

        //when
        String result = userService.signup(signupReqDto);

        //then
        assertEquals(result, "회원가입 성공");
    }

    @Test
    @DisplayName("회원가입 예외")
    void test2() {
        //given
        String nickname = "test";
        String password = "test";
        String username = "test";
        String email = "test@test.com";
        String introduce = "this is test";

        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);
        User user = new User(signupReqDto);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(signupReqDto));

        //then
        assertEquals(exception.getMessage(), "중복된 사용자가 존재합니다.");
    }

    @Test
    @DisplayName("회원탈퇴")
    void test3() {
        //given
        String nickname = "test";
        String password = "test";
        String username = "test";
        String email = "test@test.com";
        String introduce = "this is test";

        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);
        User user = new User(signupReqDto);

        WithdrawReqDto withdrawReqDto = new WithdrawReqDto(password);
        given(passwordEncoder.matches(withdrawReqDto.getPassword(), user.getPassword())).willReturn(Boolean.TRUE);
        //when
        String result = userService.withdraw(user, withdrawReqDto);


        //then
        assertEquals(result, "회원 탈퇴 성공");

    }

    @Test
    @DisplayName("회원탈퇴 예외")
    void test4() {
        //given
        String nickname = "test";
        String password = "test";
        String username = "test";
        String email = "test@test.com";
        String introduce = "this is test";

        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);
        User user = new User(signupReqDto);

        WithdrawReqDto withdrawReqDto = new WithdrawReqDto(password);
        given(passwordEncoder.matches(withdrawReqDto.getPassword(), user.getPassword())).willReturn(Boolean.FALSE);
        //when
        Exception exception =  assertThrows(IllegalArgumentException.class,
                () ->userService.withdraw(user, withdrawReqDto));


        //then
        assertEquals(exception.getMessage(), "비밀번호가 틀렸습니다.");

    }


    @Test
    @DisplayName("로그인")
    void test5() {
        //given
        String nickname = "test";
        String password = "test";
        String username = "test";
        String email = "test@test.com";
        String introduce = "this is test";

        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);
        UserServiceReqDto loginDto = new UserServiceReqDto(email, password);
        User user = new User(signupReqDto);
        HttpServletResponse res = new MockHttpServletResponse();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        String accessToken = "asd";
        given(jwtUtil.createAccessToken(user.getEmail(), user.getUserStatus())).willReturn((accessToken));
        given(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).willReturn((true));

        //when
       res = userService.login(loginDto, res);
        String result = res.getHeader("Authorization");

        //then
        assertNotNull(result);
    }


    @Test
    @DisplayName("로그아웃")
    void test6() {
        //given
        MockHttpServletRequest req = new MockHttpServletRequest();
        User user = User.builder()
                .userStatus(UserStatusEnum.NOTVERIFIED)
                .nickname("test12345678")
                .password("test12345678")
                .username("test")
                .email("test@test.com")
                .introduce("test")
                .build();
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user);
        RefreshToken refreshToken = new RefreshToken();
        String refreshTokenValue = "testRefreshToken";
        refreshToken.setRefreshToken(refreshTokenValue);
        user.setRefreshToken(refreshTokenValue);
        String accessToken = "asdasd";

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(refreshTokenService.findByRefreshToken(refreshTokenValue)).willReturn(refreshToken);
        //when
        userService.logout(req, userDetailsImpl);
        //then
        assertTrue(refreshToken.isExpired());
    }

}

