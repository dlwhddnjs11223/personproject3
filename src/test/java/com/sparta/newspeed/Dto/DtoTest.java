package com.sparta.newspeed.Dto;

import com.sparta.newspeed.dto.SignupReqDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;








public class DtoTest {

    private Validator validatorInjected;


    @BeforeEach
    void setUp() {
        validatorInjected = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("sinupRequestDto 생성 테스트")
    void test1() {
        //given
        String nickname = "test12345678";
        String password = "test12345678";
        String username = "test";
        String email = "test";
        String introduce = "test";
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);

        //when

        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);

        //then
//        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);
       assertThat(violations).isEmpty();
    }

    /*
     Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto)
     이게 validation을 통과했는지를 검증하는 메서드가 아닌지?
     */
    @Test
    @DisplayName("sinupRequestDto 생성 테스트")
    void test2() {
        //given
        String nickname = null;
        String password = "test12345678";
        String username = "test";
        String email = "test";
        String introduce = "test";
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);

        //when

        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);

        //then
//        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting("message")
                .contains("nickname은 필수입니다.");
        }

    @Test
    @DisplayName("sinupRequestDto 생성 테스트")
    void test3() {
        //given
        String nickname = "test12345678";
        String password = "tes8";
        String username = "test";
        String email = "test";
        String introduce = "test";
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);

        //when

        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);

        //then
//        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);
        assertThat(violations).hasSize(1);
        assertThat(violations)
                .extracting("message")
                .contains("password는 최소 10자 이상이어야합니다.");
    }



}
