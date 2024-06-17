package com.sparta.newspeed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
@AllArgsConstructor
public class SignupReqDto {
    @Schema(description = "사용자 ID", example = "nickname123")
    @NotBlank(message = "nickname은 필수입니다.")
    @Size(min = 10, max = 20, message = "nickname은 최소 10자 이상, 20자 이하여야합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "nickname은 알파벳 소문자(a~z), 숫자(0~9)로 구성되야합니다.")
    private String nickname; // 사용자 ID
    @Schema(description = "비밀번호", example = "password123")
    @Size(min = 10,  message = "password는 최소 10자 이상이어야합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "password는 알파벳 소문자(a~z), 숫자(0~9)로 구성되야합니다.")
    private String password; // 비밀번호
    @Schema(description = "이름", example = "John Doe")
    private String username; // 사용자 이름
    @Schema(description = "이메일", example = "john.doe@example.com")
    private String email; // 사용자 이메일
    @Schema(description = "한 줄 소개", example = "안녕하세요, 반갑습니다!")
    private String introduce; // 사용자 한 줄 소개

}
