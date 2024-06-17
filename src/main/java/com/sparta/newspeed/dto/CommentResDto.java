package com.sparta.newspeed.dto;

import com.sparta.newspeed.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "댓글 응답 DTO")
@Getter
public class CommentResDto {

    @Schema(description = "댓글 작성자의 닉네임")
    private String nickname;

    @Schema(description = "피드 ID")
    private Long PeedId;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "댓글 작성일")
    private LocalDateTime createdAt;

    @Schema(description = "댓글 수정일")
    private LocalDateTime modifiedAt;
    private int likesCount;

    public CommentResDto(Comment comment) {
        this.nickname = comment.getUser().getEmail();
        this.PeedId = comment.getPeed().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.likesCount = comment.getLikesCount();
    }
}
