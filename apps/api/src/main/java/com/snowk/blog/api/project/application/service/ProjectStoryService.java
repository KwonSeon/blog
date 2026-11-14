package com.snowk.blog.api.project.application.service;
import com.snowk.blog.api.project.infrastructure.persistence.jpa.*;
import com.snowk.blog.api.project.domain.entity.ProjectStory;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ProjectStoryService {
 private final ProjectJpaRepository projects;
 private final ProjectStoryRepository stories;
 public String read(Long id){if(!projects.existsById(id))throw missing();return stories.findById(id).map(ProjectStory::getContentMd).orElse("");}
 public String readPublic(String slug){var project=projects.findBySlugAndVisibilityAndStatus(slug,Visibility.PUBLIC,ProjectStatus.ACTIVE).orElseThrow(this::missing);return read(project.getProjectId());}
 @Transactional public void save(Long id,String content){if(!projects.existsById(id))throw missing();stories.save(new ProjectStory(id,content));}
 private BaseException missing(){return new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND);}
}
