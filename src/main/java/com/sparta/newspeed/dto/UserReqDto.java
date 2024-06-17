package com.sparta.newspeed.dto;

import lombok.Getter;

@Getter
public class UserReqDto {
    private String userId; // 사용자 ID
    private String password; // 비밀번호
    private String username; // 사용자 이름
    private String email; // 사용자 이메일
    private String introduce; // 한줄소개
}