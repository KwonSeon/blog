package com.snowk.blog.api.tag.domain.entity;

import com.snowk.blog.api.post.domain.entity.PostTag;
import com.snowk.blog.api.project.domain.entity.ProjectTag;
import com.snowk.blog.api.tag.domain.enumtype.TagKind;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import com.snowk.blog.api.common.persistence.baseentity.BaseTimeEntity;
import com.snowk.blog.api.global.config.generator.SnowflakeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "tags",
    uniqueConstraints = @UniqueConstraint(name = "uk_tags_scope_slug", columnNames = {"scope", "slug"}),
    indexes = @Index(name = "idx_tags_parent", columnList = "parent_tag_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @SnowflakeId
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private TagScope scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 20)
    private TagKind kind = TagKind.TAG;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_tag_id")
    private Tag parentTag;

    @OneToMany(mappedBy = "parentTag")
    @OrderBy("sortOrder ASC")
    private List<Tag> childTags = new ArrayList<>();

    @Column(name = "slug", nullable = false, length = 120)
    private String slug;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @OneToMany(mappedBy = "tag")
    private Set<PostTag> postTags = new HashSet<>();

    @OneToMany(mappedBy = "tag")
    private Set<ProjectTag> projectTags = new HashSet<>();
}
