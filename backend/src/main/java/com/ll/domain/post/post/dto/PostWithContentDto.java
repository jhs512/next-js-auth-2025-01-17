package com.ll.domain.post.post.dto;

import com.ll.domain.post.post.entity.Post;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class PostWithContentDto {
    @NonNull
    private long id;

    @NonNull
    private LocalDateTime createDate;

    @NonNull
    private LocalDateTime modifyDate;

    @NonNull
    private final long authorId;

    @NonNull
    private final String authorName;

    @NonNull
    private final String title;

    @NonNull
    private final String content;

    @NonNull
    private final boolean published;

    @NonNull
    private final boolean listed;

    @Setter
    private Boolean actorCanModify;

    @Setter
    private Boolean actorCanDelete;

    public PostWithContentDto(Post post) {
        this.id = post.getId();
        this.createDate = post.getCreateDate();
        this.modifyDate = post.getModifyDate();
        this.authorId = post.getAuthor().getId();
        this.authorName = post.getAuthor().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.published = post.isPublished();
        this.listed = post.isListed();
    }
}
