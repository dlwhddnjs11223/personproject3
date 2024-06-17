package com.sparta.newspeed.service;

import com.sparta.newspeed.dto.PeedRequestDto;
import com.sparta.newspeed.dto.PeedResponseDto;
import com.sparta.newspeed.dto.SignupReqDto;
import com.sparta.newspeed.entity.Peed;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.repository.PeedRepository;
import com.sparta.newspeed.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PeedServiceTest {
    @Mock
    PeedRepository peedRepository;
    @Mock
    UserService userService;

    PeedService peedService;
    PeedRequestDto setPeedRequestDto;
    User setUser;
    UserDetailsImpl setUserDetailsImpl;
    Peed setPeed;

    @BeforeEach
    void setUp() {
        peedService = new PeedService(peedRepository, userService);
        setPeedRequestDto = new PeedRequestDto("테스트");
        SignupReqDto setSignupReqDto = new SignupReqDto(
                "test12345678123",
                "1234567891",
                "종원",
                "dl@whd.dnjs",
                "ㅎㅇ");
        setUser = new User(setSignupReqDto);
        setUserDetailsImpl = new UserDetailsImpl(setUser);
        setPeed = new Peed(setPeedRequestDto, setUser);
    }

    @Test
    @DisplayName("피드 만들기 : 올바른 입력")
    void createPeedTest() {
        //given

        PeedRequestDto peedRequestDto = setPeedRequestDto;
        UserDetailsImpl userDetailsImpl = setUserDetailsImpl;
        User user = userDetailsImpl.getUser();
        given(userService.findUserByEmail(user.getEmail())).willReturn((user));
        //이러한 조건에서 (given), 이렇게 했을 때 (when), 이렇게 결과가 나온다(then)
        //when
        PeedResponseDto result = peedService.createPeed(peedRequestDto, userDetailsImpl);
        //then
        assertEquals(peedRequestDto.getContents(), result.getContents());
    }


    @Test
    @DisplayName("피드 수정하기 : 올바른 입력")
    void updatePeed() {
        //given
        Long id = 100L;
        User user = setUser;
        user.setId(100L);
        PeedRequestDto peedRequestDto = new PeedRequestDto("수정 테스트");
        given(peedRepository.findById(100L)).willReturn(Optional.of(setPeed));
//        given(peedService.findPeed(100L)).willReturn((setPeed));// 왜 이건 안되지?
        //when
        PeedResponseDto result = peedService.updatePeed(100L, peedRequestDto, user);
        //then
        assertEquals(result.getContents(), peedRequestDto.getContents());

    }

    //
    //
    //질문 : ID가 안바뀜
    @Test
    @DisplayName("피드 수정하기 : 일치하지 않는 사용자")
    void updatePeed2() {
        //given

        Long id = 100L;

        User currentUser = setUser;
        User peedUser = new User();
        peedUser.setId(101L);
        currentUser.setId(100L);


        PeedRequestDto peedRequestDto = new PeedRequestDto("수정 테스트");
        Peed peed = new Peed(peedRequestDto, peedUser);


        given(peedRepository.findById(100L)).willReturn(Optional.of(peed));
        //when
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    peedService.updatePeed(100L, peedRequestDto, currentUser);
                }
        );

        //then
        assertEquals("사용자가 일치하지 않습니다.", exception.getMessage());

    }

    @Test
    void deletePeed() {
    }

    @Test
    void getAllPeeds() {
    }
}