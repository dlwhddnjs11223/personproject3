package com.sparta.newspeed.service;

import com.sparta.newspeed.dto.CommentReqDto;
import com.sparta.newspeed.dto.CommentResDto;
import com.sparta.newspeed.entity.Comment;
import com.sparta.newspeed.entity.Peed;
import com.sparta.newspeed.entity.User;
import com.sparta.newspeed.repository.CommentRepository;
import com.sparta.newspeed.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PeedService peedService;


    public void addComment(Long newspeedId, CommentReqDto commentReqDto, UserDetailsImpl userDetails) {
        User user = userService.findUserByEmail(userDetails.getUser().getEmail());
        Peed peed = peedService.findPeed(newspeedId);
        Comment comment = new Comment(commentReqDto, user, peed);

        commentRepository.save(comment);

    }

    public CommentResDto getComment(Long newspeedId, Long commentId) {
        Comment comment = findCommentByIdAndCommentId(newspeedId, commentId);

        return new CommentResDto(comment);

    }

    @Transactional
    public CommentResDto mdofiyComment(Long newspeedId, Long commentId, CommentReqDto commentReqDto, UserDetailsImpl userDetails) {

        Comment comment = findCommentByIdAndCommentId(newspeedId, commentId);

        checkUserComment(comment, userDetails);

        comment.setContent(commentReqDto.getContent());
        return new CommentResDto(commentRepository.save(comment));
    }

    public void deleteComment(Long newspeedId, Long commentId, UserDetailsImpl userDetails) {
        Comment comment = findCommentByIdAndCommentId(newspeedId, commentId);

        checkUserComment(comment, userDetails);

        commentRepository.delete(comment);
    }


    public Comment findCommentByIdAndCommentId(Long newspeedId, Long commentId){
        return commentRepository
                .findByPeedIdAndId(newspeedId, commentId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 댓글입니다."));
    }

    public void checkUserComment(Comment comment, UserDetailsImpl userDetails) {
        if(!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }
    }


    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(()-> new IllegalTransactionStateException("등록되지 않은 댓글입니다."));
    }
}



