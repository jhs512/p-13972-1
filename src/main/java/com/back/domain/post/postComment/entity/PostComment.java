package com.back.domain.post.postComment.entity;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.global.exception.ServiceException;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostComment extends BaseEntity {
    @ManyToOne
    private Member author;
    @ManyToOne
    private Post post;
    private String content;

    public PostComment(Member author, Post post, String content) {
        this.author = author;
        this.post = post;
        this.content = content;
    }

    public void modify(String content) {
        this.content = content;
    }

    public void checkActorCanModify(Member actor) {
        if (!author.equals(actor))
            throw new ServiceException("403-1", "%d번 댓글 수정권한이 없습니다.".formatted(getId()));
    }

    public void checkActorCanDelete(Member actor) {
        if (!author.equals(actor))
            throw new ServiceException("403-2", "%d번 댓글 삭제권한이 없습니다.".formatted(getId()));
    }
}