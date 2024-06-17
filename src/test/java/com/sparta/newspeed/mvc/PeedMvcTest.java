package com.sparta.newspeed.mvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newspeed.controller.PeedController;
import com.sparta.newspeed.controller.UserController;
import com.sparta.newspeed.dto.PeedRequestDto;
import com.sparta.newspeed.entity.Peed;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import com.sparta.newspeed.repository.PeedRepository;
import com.sparta.newspeed.security.UserDetailsImpl;
import com.sparta.newspeed.security.WebSecurityConfig;
import com.sparta.newspeed.service.PeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Optional;

import static org.awaitility.Awaitility.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {PeedController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)

public class PeedMvcTest {


    private MockMvc mvc;

    private Principal mockPrincipal; // 가짜 인증

    @Autowired
    private WebApplicationContext context; //

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PeedService peedService;

    @MockBean
    private PeedRepository peedRepository;


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
    @DisplayName("피드 작성")
    void test() throws Exception {
        //given
        mockUserSetup();
        String contents = "testContents";
        PeedRequestDto peedRequestDto = new PeedRequestDto(contents);

        String postInfo = objectMapper.writeValueAsString(peedRequestDto);

        //when-then
        mvc.perform(post("/api/peeds")
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal((mockPrincipal))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("피드 조회")
    void test2() throws Exception {
        int page = 1;
        int size = 10;
        String sortBy = "id";
        boolean isAsc = true;
        Long peedId = 1L;


        mvc.perform(get("/api/peeds")
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .queryParam("sortBy", sortBy)
                        .queryParam("isAsc", String.valueOf(isAsc))
                )
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("피드 수정")
        // 왜 안되는건지 모르겠음, pathVariable 의 파라미터값은 mvc에서 어떻게 전달?
    void test3() throws Exception {
        mockUserSetup();

        String nickname = "testnickname1"; // 사용자 ID
        String password = "1234567890";// 비밀번호
        String username = "test"; // "사용자 이름
        String email = "test@test.test";// 사용자 이메일
        String introduce = "It's a test"; // 한줄소개
        UserStatusEnum userStatus = UserStatusEnum.NOTVERIFIED; // 회원 상태코드
        User user = new User(nickname, password, username, email, introduce, userStatus);
        user.setId(1L);

        Long peedId = 1L;
        Peed peed = new Peed();
        peed.setUser(user);

        String contents = "수정 테스트";
        PeedRequestDto peedRequestDto = new PeedRequestDto(contents);
        Peed updatepeed = new Peed(peedRequestDto);
//        BDDMockito.given(peedRepository.findById(peedId)).willReturn(Optional.of(peed));


        String putInfo = objectMapper.writeValueAsString(peedRequestDto);

        mvc.perform(put("/api/peeds/" + peedId)
                        .content(putInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal((mockPrincipal))
                )
                .andExpect(status().is2xxSuccessful())


                .andDo(print());


    }
}
