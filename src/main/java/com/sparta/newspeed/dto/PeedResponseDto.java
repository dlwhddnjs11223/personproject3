package com.sparta.newspeed.dto;

import com.sparta.newspeed.entity.Peed;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "피드 응답 DTO")
public class PeedResponseDto {
    @Schema(description = "피드 ID")
    private Long id;

    @Schema(description = "작성자 닉네임")
    private String nickname;

    @Schema(description = "피드 내용")
    private String contents;

    @Schema(description = "작성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime modifiedAt;
    private int likeCount;


    public PeedResponseDto(Peed savepeed) {
        this.id = savepeed.getId();
        this.nickname = savepeed.getEmail();
        this.contents = savepeed.getContents();
        this.createdAt = savepeed.getCreatedAt();
        this.modifiedAt = savepeed.getModifiedAt();
        this.likeCount = savepeed.getLikesCount();

    }
}
