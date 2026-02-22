package com.snowk.blog.api.project.domain.entity;

import com.snowk.blog.api.post.domain.entity.PostProject;
import com.snowk.blog.api.global.config.generator.SnowflakeId;
import com.snowk.blog.api.global.common.baseentity.BaseTimeEntity;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
    name = "projects",
    uniqueConstraints = @UniqueConstraint(name = "uk_projects_slug", columnNames = "slug"),
    indexes = @Index(name = "idx_projects_visibility", columnList = "visibility")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseTimeEntity {

    @Id
    @SnowflakeId
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "slug", nullable = false, length = 120)
    private String slug;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "service_url", length = 500)
    private String serviceUrl;

    @Column(name = "repo_url", length = 500)
    private String repoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private Visibility visibility = Visibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @Column(name = "cover_media_asset_id")
    private Long coverMediaAssetId;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "project")
    private Set<ProjectTag> projectTags = new HashSet<>();

    @OneToMany(mappedBy = "project")
    private Set<PostProject> postProjects = new HashSet<>();
}
