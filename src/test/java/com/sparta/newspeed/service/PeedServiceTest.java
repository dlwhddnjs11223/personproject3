package com.sparta.newspeed.service;

import com.sparta.newspeed.dto.PeedRequestDto;
import com.sparta.newspeed.dto.PeedResponseDto;
import com.sparta.newspeed.dto.SignupReqDto;
import com.sparta.newspeed.entity.Peed;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.entity.UserStatusEnum;
import com.sparta.newspeed.repository.PeedRepository;
import com.sparta.newspeed.repository.UserRepository;
import com.sparta.newspeed.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeedServiceTest {
    @Mock
    private PeedRepository peedRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    PeedService peedService;

    @BeforeEach
    void setUp() {
//        peedService = new PeedService(peedRepository, userService);

    }


    @Test
    @DisplayName("피드 작성")
    void test1() {
       //given
        String contents = "testContents";
        PeedRequestDto peedRequestDto = new PeedRequestDto(contents);

        User user = User.builder()
                .userStatus(UserStatusEnum.NOTVERIFIED)
                .nickname("test12345678")
                .password("test12345678")
                .username("test")
                .email("test@test.com")
                .introduce("test")
                .build();

        User mock = Mockito.mock(User.class);

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user);

//        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(userService.findUserByEmail(anyString())).willReturn(user);
        //when
        PeedResponseDto result = peedService.createPeed(peedRequestDto, userDetailsImpl);


        //then
        assertEquals(contents, result.getContents());

    }


}