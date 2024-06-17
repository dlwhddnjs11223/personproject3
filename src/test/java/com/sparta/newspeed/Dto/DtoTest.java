package com.sparta.newspeed.Dto;

import com.sparta.newspeed.dto.SignupReqDto;
import com.sparta.newspeed.mvc.MockSpringSecurityFilter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;








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
        String nickname = "test";
        String password = "test";
        String username = "test";
        String email = "test";
        String introduce = "test";

        //when
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);


        //then
        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);
        for (ConstraintViolation<SignupReqDto> violation : violations) {
            System.err.println(violation.getMessage());
        }
    }

    /*
     Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto)
     이게 validation을 통과했는지를 검증하는 메서드가 아닌지?
     */
    @Test
    @DisplayName("sinupRequestDto 생성 테스트")
    void test2() {
        //given
        String nickname = "test12345678";
        String password = "test12345678";
        String username = "test";
        String email = "test";
        String introduce = "test";

        //when
        SignupReqDto signupReqDto = new SignupReqDto(nickname, password, username, email, introduce);


        //then
        Set<ConstraintViolation<SignupReqDto>> violations = validatorInjected.validate(signupReqDto);
        for (ConstraintViolation<SignupReqDto> violation : violations) {
            System.err.println(violation.getMessage());
        }
    }


}
