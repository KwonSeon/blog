package com.snowk.blog.api.post.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostProjectId implements Serializable {

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    public PostProjectId(Long postId, Long projectId) {
        this.postId = postId;
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostProjectId that)) {
            return false;
        }
        return Objects.equals(postId, that.postId) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, projectId);
    }
}
