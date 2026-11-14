package com.snowk.blog.api.project.application.service;
import com.snowk.blog.api.project.infrastructure.persistence.jpa.*;
import com.snowk.blog.api.project.domain.entity.ProjectStory;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.exception.BaseException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ProjectStoryServiceTest {
 final ProjectJpaRepository projects=mock(ProjectJpaRepository.class);
 final ProjectStoryRepository stories=mock(ProjectStoryRepository.class);
 final ProjectStoryService service=new ProjectStoryService(projects,stories);
 @Test void privateOrInactiveProjectCannotExposeStory(){when(projects.findBySlugAndVisibilityAndStatus("private",Visibility.PUBLIC,ProjectStatus.ACTIVE)).thenReturn(Optional.empty());assertThrows(BaseException.class,()->service.readPublic("private"));verifyNoInteractions(stories);}
 @Test void missingProjectCannotCreateOrphanStory(){assertThrows(BaseException.class,()->service.save(1L,"content"));verifyNoInteractions(stories);}
 @Test void existingStoryPreservesMarkdownAndImageReference(){when(projects.existsById(1L)).thenReturn(true);when(stories.findById(1L)).thenReturn(Optional.of(new ProjectStory(1L,"## 기능\n![화면](/media/assets/9001/content)")));assertTrue(service.read(1L).contains("/media/assets/9001/content"));}
 @Test void existingProjectWithoutStoryIsEmpty(){when(projects.existsById(1L)).thenReturn(true);when(stories.findById(1L)).thenReturn(Optional.empty());assertEquals("",service.read(1L));}
}
