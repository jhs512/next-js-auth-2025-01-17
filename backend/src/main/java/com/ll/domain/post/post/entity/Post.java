package com.ll.domain.post.post.entity;

import com.ll.domain.member.member.entity.Member;
import com.ll.domain.post.comment.entity.PostComment;
import com.ll.global.exceptions.ServiceException;
import com.ll.global.jpa.entity.BaseTime;
import com.ll.global.rsData.RsData;
import com.ll.standard.base.Empty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<PostComment> comments = new ArrayList<>();

    private boolean published;

    private boolean listed;

    public PostComment addComment(Member author, String content) {
        PostComment comment = PostComment.builder()
                .post(this)
                .author(author)
                .content(content)
                .build();

        comments.add(comment);

        return comment;
    }

    public List<PostComment> getCommentsByOrderByIdDesc() {
        return comments.reversed();
    }

    public Optional<PostComment> getCommentById(long commentId) {
        return comments.stream()
                .filter(comment -> comment.getId() == commentId)
                .findFirst();
    }

    public void removeComment(PostComment postComment) {
        comments.remove(postComment);
    }


    public RsData<Empty> getCheckActorCanDeleteRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");

        if (actor.isAdmin()) return RsData.OK;

        if (actor.equals(author)) return RsData.OK;

        return new RsData<>("403-1", "작성자만 글을 삭제할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        Optional.of(
                        getCheckActorCanDeleteRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getResultCode(), rsData.getMsg());
                });
    }


    public RsData<Empty> getCheckActorCanModifyRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");

        if (actor.equals(author)) return RsData.OK;

        return new RsData<>("403-1", "작성자만 글을 수정할 수 있습니다.");
    }

    public void checkActorCanModify(Member actor) {
        Optional.of(
                        getCheckActorCanModifyRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getResultCode(), rsData.getMsg());
                });
    }


    public RsData<Empty> getCheckActorCanReadRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");

        if (actor.isAdmin()) return RsData.OK;

        if (actor.equals(author)) return RsData.OK;

        return new RsData<>("403-1", "비공개글은 작성자만 볼 수 있습니다.");
    }

    public void checkActorCanRead(Member actor) {
        Optional.of(
                        getCheckActorCanReadRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getResultCode(), rsData.getMsg());
                });
    }
}
