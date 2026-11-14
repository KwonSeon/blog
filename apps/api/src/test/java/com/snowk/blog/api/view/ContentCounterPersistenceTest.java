package com.snowk.blog.api.view;
import com.snowk.blog.api.post.domain.entity.Post;
import com.snowk.blog.api.project.domain.entity.Project;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest @ActiveProfiles("test") @Transactional
class ContentCounterPersistenceTest {
 @Autowired EntityManager em; @Autowired JdbcTemplate jdbc;
 @Test void stalePostEditorDoesNotOverwriteConcurrentCounters() {
  var post=Post.create("counter-post","Before","excerpt","body",Visibility.PUBLIC,PostStatus.PUBLISHED,"ko",null,null,null);em.persist(post);em.flush();
  jdbc.update("UPDATE posts SET view_count=7,like_count=3 WHERE post_id=?",post.getPostId());
  post.update("counter-post","After","excerpt","new body",Visibility.PUBLIC,"ko",null);em.flush();
  assertEquals(7,jdbc.queryForObject("SELECT view_count FROM posts WHERE post_id=?",Long.class,post.getPostId()));
  assertEquals(3,jdbc.queryForObject("SELECT like_count FROM posts WHERE post_id=?",Long.class,post.getPostId()));
 }
 @Test void staleProjectEditorDoesNotOverwriteConcurrentCounters() {
  var project=Project.create("counter-project","Before","summary",null,null,Visibility.PUBLIC,ProjectStatus.ACTIVE,null,null);em.persist(project);em.flush();
  jdbc.update("UPDATE projects SET view_count=7,like_count=3 WHERE project_id=?",project.getProjectId());
  project.update("counter-project","After","new summary",null,null,Visibility.PUBLIC,ProjectStatus.ACTIVE,null);em.flush();
  assertEquals(7,jdbc.queryForObject("SELECT view_count FROM projects WHERE project_id=?",Long.class,project.getProjectId()));
  assertEquals(3,jdbc.queryForObject("SELECT like_count FROM projects WHERE project_id=?",Long.class,project.getProjectId()));
 }
}
