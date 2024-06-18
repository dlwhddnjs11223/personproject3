package com.sparta.newspeed.entity;

import com.sparta.newspeed.Timestamped;
import com.sparta.newspeed.dto.SignupReqDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="user")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키
    @Column(nullable = false, unique = true)
    private String nickname; // 사용자 ID
    @Column(nullable = false)
    private String password; // 비밀번호
    @Column(nullable = false)
    private String username; // 사용자 이름
    @Column(nullable = false)
    private String email; // 사용자 이메일
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime departureTime = null;
    @Column(nullable = false)
    private String introduce; // 한줄소개
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatusEnum userStatus; // 회원 상태코드
    @Column(nullable = true)
    private String refreshToken=null;

    @OneToMany(mappedBy="user")
    private List<Peed> peedlist = new ArrayList<>();

    @OneToMany(mappedBy="user")
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy="user")
    private List<Likes> likesList = new ArrayList<>();

    public User(SignupReqDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.password = requestDto.getPassword();
        this.username = requestDto.getUsername();
        this.email = requestDto.getEmail();
        this.introduce =requestDto.getIntroduce();
        this.userStatus = UserStatusEnum.NOTVERIFIED;
        this.departureTime = LocalDateTime.now();
    }

    @Builder
    public User(String nickname, String password, String username, String email, String introduce, UserStatusEnum userStatus) {
      this.nickname = nickname;
      this.password = password;
      this.username = username;
      this.email = email;
      this.introduce = introduce;
      this.userStatus = userStatus;

    }

    public void withdraw() {
        this.userStatus = UserStatusEnum.WITHDREW;
    }




}

//    private RefreshToken refreshToken; // 리프레쉬 토큰
//
//    private Content Content; // 게시물 외래키




