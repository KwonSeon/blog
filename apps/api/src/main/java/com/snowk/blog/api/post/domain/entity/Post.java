package com.snowk.blog.api.post.domain.entity;

import com.snowk.blog.api.global.config.generator.SnowflakeId;
import com.snowk.blog.api.global.common.baseentity.BaseTimeEntity;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "posts",
    uniqueConstraints = @UniqueConstraint(name = "uk_posts_slug", columnNames = "slug"),
    indexes = {
        @Index(name = "idx_posts_status_published", columnList = "status,published_at"),
        @Index(name = "idx_posts_visibility", columnList = "visibility"),
        @Index(name = "idx_posts_author", columnList = "author_user_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @SnowflakeId
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "slug", nullable = false, length = 150)
    private String slug;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "excerpt", length = 600)
    private String excerpt;

    @Lob
    @Column(name = "content_md", nullable = false, columnDefinition = "LONGTEXT")
    private String contentMd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PostStatus status = PostStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "lang", nullable = false, length = 10)
    private String lang = "ko";

    @Column(name = "cover_media_asset_id")
    private Long coverMediaAssetId;

    @Column(name = "author_user_id")
    private Long authorUserId;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "post")
    private Set<PostTag> postTags = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostProject> postProjects = new HashSet<>();
}
