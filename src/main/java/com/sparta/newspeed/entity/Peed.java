package com.sparta.newspeed.entity;

import com.sparta.newspeed.dto.PeedRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity // JPA가 관리할 수 있는 Entity 클래스 지정
@Getter
@NoArgsConstructor
@Setter
@Table(name = "newspeed")// 매핑할 테이블의 이름을 지정
public class Peed extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Column(name = "user_id", nullable = false, length = 500)// 유저테이블에서 id만 가져오기
//    private String user_id;
    @Column(length = 500)
    private String email;
    @Column(name = "contents", nullable = false, length = 500)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy="peed")
    private List<Comment> commentList = new ArrayList<>();

    private int likesCount;



    public Peed(PeedRequestDto requestDto) {

        this.contents = requestDto.getContents();
    }

    public Peed(PeedRequestDto requestDto, User user) {
        this.email = user.getEmail();
        this.contents = requestDto.getContents();
        this.user = user;
        user.getPeedlist().add(this);
    }

    public void update(PeedRequestDto requestDto) {
        this.contents = requestDto.getContents();

    }

    public void likesCount (String oper) {
        if(oper.equals("+")) {
            this.likesCount++;
        } else if(oper.equals("-")) {
            this.likesCount--;
        }

    }
}

