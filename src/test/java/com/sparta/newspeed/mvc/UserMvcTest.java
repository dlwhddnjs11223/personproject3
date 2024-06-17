package com.sparta.newspeed.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newspeed.controller.UserController;
import com.sparta.newspeed.dto.SignupReqDto;
import com.sparta.newspeed.dto.UserServiceReqDto;
import com.sparta.newspeed.dto.WithdrawReqDto;
import com.sparta.newspeed.email.EmailService;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import com.sparta.newspeed.security.UserDetailsImpl;
import com.sparta.newspeed.security.WebSecurityConfig;
import com.sparta.newspeed.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)

public class UserMvcTest {


    private MockMvc mvc;

    private Principal mockPrincipal; // 가짜 인증

    @Autowired
    private WebApplicationContext context; //

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailService emailService;

//    @MockBean
//    private PasswordEncoder passwordEncoder;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        // Mock 테스트 유져 생성
        Long id = 1L;
        String nickname = "testnickname1"; // 사용자 ID
        String password = "1234567890";// 비밀번호
        String username = "test"; // "사용자 이름
        String email = "test@test.test";// 사용자 이메일
        String introduce = "It's a test"; // 한줄소개
        UserStatusEnum userStatus = UserStatusEnum.NOTVERIFIED; // 회원 상태코드
        User testUser = new User(nickname, password, username, email, introduce, userStatus);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @Test
    @DisplayName("회원가입")
    void test1() throws Exception {
        // given
        String nickname = "testnickname1"; // 사용자 ID
        String password = "1234567890";// 비밀번호
        String username = "test"; // "사용자 이름
        String email = "test@test.test";// 사용자 이메일
        String introduce = "It's a test"; // 한줄소개
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);


        String postInfo = objectMapper.writeValueAsString(signupReqDto);

        // when - then
        mvc.perform(post("/user/signup")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("회원탈퇴")
        // 뭐가 잘못된건지?
    void test2() throws Exception {
        // given
        mockUserSetup();
        String password = "1234567890";
        WithdrawReqDto withdrawReqDto = new WithdrawReqDto(password);


        String postInfo = objectMapper.writeValueAsString(withdrawReqDto);

        // when - then
        mvc.perform(put("/user/withdraw")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인")
        // 로그인이 어떻게 된거지? 데이터베이스에 해당 데이터가 없는데
    void test3() throws Exception {
        // given
        String email = "test@test.com";
        String password = "1234567890";
        UserServiceReqDto userServiceReqDto = new UserServiceReqDto(email, password);


        String nickname = "testnickname1"; // 사용자 ID
        String password2 = "1234567890";// 비밀번호
        String username = "test"; // "사용자 이름
        String email2 = "test@test.test";// 사용자 이메일
        String introduce = "It's a test"; // 한줄소개
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);
        User user = new User(signupReqDto);

        given(userService.findUserByEmail(email)).willReturn(user);

        String postInfo = objectMapper.writeValueAsString(userServiceReqDto);

        // when - then
        mvc.perform(post("/user/login")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃")
        // 로그인이 어떻게 된거지? 데이터베이스에 해당 데이터가 없는데
    void test4() throws Exception {
        // given
        mockUserSetup();




        // when - then
        mvc.perform(post("/user/logout")

                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)

                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }


}


