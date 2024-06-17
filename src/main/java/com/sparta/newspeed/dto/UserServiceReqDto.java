package com.sparta.newspeed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청 DTO")
@AllArgsConstructor
public class UserServiceReqDto {

    @Schema(description = "사용자 이메일", example = "user123@asdsad.com")
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
