package com.sparta.newspeed.service;

import com.sparta.newspeed.dto.SignupReqDto;
import com.sparta.newspeed.dto.UserServiceReqDto;
import com.sparta.newspeed.dto.WithdrawReqDto;
import com.sparta.newspeed.email.EmailService;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import com.sparta.newspeed.repository.UserRepository;
import com.sparta.newspeed.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    //    @Mock
//    PasswordEncoder passwordEncoder;
    @Mock
    UserRepository userRepository;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    LogoutAccessTokenService LogoutAccessTokenService;
    @Mock
    EmailService emailService;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;
    SignupReqDto setSignupReqDto;
    User setUser;
    WithdrawReqDto setWithdrawReqDto;

    UserServiceReqDto setLoginReqDto;
    MockHttpServletResponse mockHttpServletResponse;


    @BeforeEach
    void setUp() {

        jwtUtil.init();
//        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(
                passwordEncoder,
                userRepository,
                refreshTokenService,
                LogoutAccessTokenService,
                emailService,
                jwtUtil);

        setSignupReqDto = new SignupReqDto(
                "test12345678123",
                "1234567891",
                "종원",
                "dl@whd.dnjs",
                "ㅎㅇ");
        setUser = new User(setSignupReqDto);
        setWithdrawReqDto = new WithdrawReqDto("1234567891");
        setLoginReqDto = new UserServiceReqDto("dl@whd.dnjs", "1234567891");
        mockHttpServletResponse = new MockHttpServletResponse();
    }

    ;


    @Test
    @DisplayName("회원 가입 : 중복되지 않은 이메일 ")
    void signupTest() {
        //given
        Long userId = 100L;
        SignupReqDto signupReqDto = setSignupReqDto;
        signupReqDto.setPassword(passwordEncoder.encode(signupReqDto.getPassword()));

        //when
        String result = userService.signup(signupReqDto);


        //then
        assertEquals("회원가입 성공", result);
    }

    @Test
    @DisplayName("회원 가입 : 중복된 이메일 ")
    void signupTest2() {
        //given
        String userEmail = "dl@whd.dnjs";
        SignupReqDto signupReqDto = setSignupReqDto;
        signupReqDto.setPassword(passwordEncoder.encode(signupReqDto.getPassword()));

        User user = new User(signupReqDto);


        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));

        //when

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    String result = userService.signup(signupReqDto);
                }
        );

        //then
        assertEquals("중복된 사용자가 존재합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 : 올바른 입력")
    public void withdrawTest1() {
        //given
        User user = setUser;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        WithdrawReqDto withdrawReqDto = setWithdrawReqDto;
        //when
        String result = userService.withdraw(setUser, setWithdrawReqDto);
        //then
        assertEquals("회원 탈퇴 성공", result);
    }

    @Test
    @DisplayName("회원 탈퇴 :  잘못된 비밀번호 입력")
    public void withdrawTest2() {
        //given
        User user = setUser;
        user.setPassword(passwordEncoder.encode("123456789"));
        WithdrawReqDto withdrawReqDto = setWithdrawReqDto;
        //when

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    String result = userService.withdraw(setUser, setWithdrawReqDto);
                }
        );
        //then
        assertEquals("비밀번호가 틀렸습니다.", exception.getMessage());
    }

    /*
    액세스 토큰이 안만들어지는데 이유가 뭔지
     */
    @Test
    @DisplayName("로그인 : 올바른 입력")
    void loginTest() {
        //given

        User user = setUser;

        UserServiceReqDto loginDto = setLoginReqDto;
        MockHttpServletResponse res = mockHttpServletResponse;

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(user.getEmail(), user.getUserStatus())).thenReturn("asdasdasd");
        when(refreshTokenService.createRefreshToken(user)).thenReturn("asdasdasdasd");

        // When
        userService.login(loginDto, res);

        // Then
        verify(jwtUtil).addAccessJwtToHeader(eq("asdasdasd"), any(HttpServletResponse.class));
        verify(jwtUtil).addRefreshJwtToHeader(eq("asdasdasdasd"), any(HttpServletResponse.class));


    }


    @Test
    @DisplayName("로그인 : 잘못된 비밀번호 입력")
    void loginTest2() {
        //given

        User user = setUser;
        user.setPassword(passwordEncoder.encode("12345678910"));
        UserServiceReqDto loginDto = setLoginReqDto;
        MockHttpServletResponse res = mockHttpServletResponse;

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        //when

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    userService.login(loginDto, res);
                }
        );


        //then

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());

    }


    @Test
    @DisplayName("로그인 : 탈퇴한 회원정보 입력")
    void loginTest3() {
        //given

        User user = setUser;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserStatus(UserStatusEnum.WITHDREW);
        UserServiceReqDto loginDto = setLoginReqDto;
        MockHttpServletResponse res = mockHttpServletResponse;

        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        //when

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    userService.login(loginDto, res);
                }
        );


        //then

        assertEquals("탈퇴한 회원입니다.", exception.getMessage());

    }


}

