package com.ll.domain.member.member.entity;

import com.ll.domain.post.post.entity.Post;
import com.ll.global.jpa.entity.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTime {
    @Column(unique = true, length = 30)
    private String username;

    @Column(length = 50)
    private String password;

    @Column(length = 30)
    private String nickname;

    @Column(unique = true, length = 50)
    private String apiKey;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    public String getName() {
        return nickname;
    }

    public boolean isAdmin() {
        return "admin".equals(username);
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public Member(long id, String username) {
        this.setId(id);
        this.username = username;
    }

    public void addPost(Post post) {
        post.setAuthor(this);
        posts.add(post);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthoritiesAsStringList()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public List<String> getAuthoritiesAsStringList() {
        List<String> authorities = new ArrayList<>();

        if (isAdmin())
            authorities.add("ROLE_ADMIN");

        return authorities;
    }
}

