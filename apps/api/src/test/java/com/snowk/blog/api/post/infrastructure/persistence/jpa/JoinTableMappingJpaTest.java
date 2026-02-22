package com.snowk.blog.api.post.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.post.domain.entity.PostProject;
import com.snowk.blog.api.post.domain.entity.PostProjectId;
import com.snowk.blog.api.post.domain.entity.PostTag;
import com.snowk.blog.api.post.domain.entity.PostTagId;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.project.domain.entity.ProjectTag;
import com.snowk.blog.api.project.domain.entity.ProjectTagId;
import com.snowk.blog.api.project.infrastructure.persistence.jpa.ProjectJpaRepository;
import com.snowk.blog.api.support.ReflectionEntityFactory;
import com.snowk.blog.api.tag.domain.entity.Tag;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import com.snowk.blog.api.tag.infrastructure.persistence.jpa.TagJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JoinTableMappingJpaTest {

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private ProjectJpaRepository projectJpaRepository;

    @Autowired
    private TagJpaRepository tagJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("post_tags 중간 테이블 매핑으로 연관을 저장/조회할 수 있다")
    void postTag_canPersistAndFind() {
        Post post = postJpaRepository.save(createPost("post-tag-post", "Post for post_tags"));
        Tag tag = tagJpaRepository.save(createTag(TagScope.POST, "post-tag", "PostTag"));

        PostTag postTag = ReflectionEntityFactory.instantiate(PostTag.class);
        ReflectionEntityFactory.setField(postTag, "postTagId", new PostTagId(post.getPostId(), tag.getTagId()));
        ReflectionEntityFactory.setField(postTag, "post", post);
        ReflectionEntityFactory.setField(postTag, "tag", tag);

        entityManager.persist(postTag);
        entityManager.flush();
        entityManager.clear();

        PostTag loaded = entityManager.find(PostTag.class, new PostTagId(post.getPostId(), tag.getTagId()));
        assertThat(loaded).isNotNull();
    }

    @Test
    @DisplayName("post_projects 중간 테이블 매핑으로 연관을 저장/조회할 수 있다")
    void postProject_canPersistAndFind() {
        Post post = postJpaRepository.save(createPost("post-project-post", "Post for post_projects"));
        Project project = projectJpaRepository.save(createProject("post-project", "PostProject"));

        PostProject postProject = ReflectionEntityFactory.instantiate(PostProject.class);
        ReflectionEntityFactory.setField(
            postProject,
            "postProjectId",
            new PostProjectId(post.getPostId(), project.getProjectId())
        );
        ReflectionEntityFactory.setField(postProject, "post", post);
        ReflectionEntityFactory.setField(postProject, "project", project);

        entityManager.persist(postProject);
        entityManager.flush();
        entityManager.clear();

        PostProject loaded = entityManager.find(
            PostProject.class,
            new PostProjectId(post.getPostId(), project.getProjectId())
        );
        assertThat(loaded).isNotNull();
    }

    @Test
    @DisplayName("project_tags 중간 테이블 매핑으로 연관을 저장/조회할 수 있다")
    void projectTag_canPersistAndFind() {
        Project project = projectJpaRepository.save(createProject("project-tag-project", "ProjectTag"));
        Tag tag = tagJpaRepository.save(createTag(TagScope.PROJECT, "project-tag", "ProjectTag"));

        ProjectTag projectTag = ReflectionEntityFactory.instantiate(ProjectTag.class);
        ReflectionEntityFactory.setField(
            projectTag,
            "projectTagId",
            new ProjectTagId(project.getProjectId(), tag.getTagId())
        );
        ReflectionEntityFactory.setField(projectTag, "project", project);
        ReflectionEntityFactory.setField(projectTag, "tag", tag);

        entityManager.persist(projectTag);
        entityManager.flush();
        entityManager.clear();

        ProjectTag loaded = entityManager.find(
            ProjectTag.class,
            new ProjectTagId(project.getProjectId(), tag.getTagId())
        );
        assertThat(loaded).isNotNull();
    }

    @Test
    @DisplayName("post_tags 동일한 (post_id, tag_id) 조합은 중복 저장할 수 없다")
    void postTag_duplicateId_throwsPersistenceException() {
        Post post = postJpaRepository.save(createPost("post-tag-dup-post", "Dup PostTag"));
        Tag tag = tagJpaRepository.save(createTag(TagScope.POST, "post-tag-dup", "DupTag"));

        PostTag first = ReflectionEntityFactory.instantiate(PostTag.class);
        ReflectionEntityFactory.setField(first, "postTagId", new PostTagId(post.getPostId(), tag.getTagId()));
        ReflectionEntityFactory.setField(first, "post", post);
        ReflectionEntityFactory.setField(first, "tag", tag);
        entityManager.persist(first);
        entityManager.flush();
        entityManager.clear();

        PostTag second = ReflectionEntityFactory.instantiate(PostTag.class);
        ReflectionEntityFactory.setField(second, "postTagId", new PostTagId(post.getPostId(), tag.getTagId()));
        ReflectionEntityFactory.setField(second, "post", post);
        ReflectionEntityFactory.setField(second, "tag", tag);

        assertThatThrownBy(() -> {
            entityManager.persist(second);
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    private Post createPost(String slug, String title) {
        Post post = ReflectionEntityFactory.instantiate(Post.class);
        ReflectionEntityFactory.setField(post, "slug", slug);
        ReflectionEntityFactory.setField(post, "title", title);
        ReflectionEntityFactory.setField(post, "contentMd", "# content");
        return post;
    }

    private Project createProject(String slug, String title) {
        Project project = ReflectionEntityFactory.instantiate(Project.class);
        ReflectionEntityFactory.setField(project, "slug", slug);
        ReflectionEntityFactory.setField(project, "title", title);
        return project;
    }

    private Tag createTag(TagScope scope, String slug, String name) {
        Tag tag = ReflectionEntityFactory.instantiate(Tag.class);
        ReflectionEntityFactory.setField(tag, "scope", scope);
        ReflectionEntityFactory.setField(tag, "slug", slug);
        ReflectionEntityFactory.setField(tag, "name", name);
        return tag;
    }
}
