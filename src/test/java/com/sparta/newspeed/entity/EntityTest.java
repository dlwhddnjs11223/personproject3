package com.sparta.newspeed.entity;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Nested
    @DisplayName("댓글 좋아요 기능 테스트")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CommentTest {
        Comment comment;
        String oper;

        @Order(1)
        @Test
        @DisplayName("댓글 좋아요 테스트")
        void CommentLikesCountTest1() {
            //given
            oper = "+";
            comment = new Comment();

            //when
            comment.likesCount(oper);

            //then
            assertTrue(comment.getLikesCount() == 1 );
        }


        @Order(2)
        @Test
        @DisplayName("댓글 좋아요 취소 테스트")
        void CommentLikesCountTest2() {
            //given
            oper = "-";

            //when
            comment.likesCount(oper);

            //then
            assertTrue(comment.getLikesCount() == 0 );

        }
    }


    @Nested
    @DisplayName("게시물 좋아요 기능 테스트")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FeedTest {
        Peed peed;
        String oper;

        @Order(1)
        @Test
        @DisplayName("게시물 좋아요 테스트")
        void FeedLikeCountTest1() {
            //given
            oper = "+";
            peed = new Peed();

            //when
            peed.likesCount(oper);

            //then
            assertTrue(peed.getLikesCount() == 1 );
        }


        @Order(2)
        @Test
        @DisplayName("댓글 좋아요 취소 테스트")
        void CommentLikesCountTest2() {
            //given
            oper = "-";

            //when
            peed.likesCount(oper);

            //then
            assertTrue(peed.getLikesCount() == 0 );

        }
    }


}
