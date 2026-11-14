package com.snowk.blog.api.settings;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.snowk.blog.api.global.exception.BaseException;
class BlogSettingsServiceTest {
 final BlogSettingsRepository repository=mock(BlogSettingsRepository.class);
 final BlogSettingsService service=new BlogSettingsService(repository);
 SettingsView input(boolean hero,boolean posts,boolean projects,long revision,String github){return new SettingsView("내 블로그","새로운 제목","설명",github,SettingsView.HomeOrder.PROJECTS_FIRST,SettingsView.PostLayout.LIST,SettingsView.ContentWidth.WIDE,hero,posts,projects,revision);}
 @Test void savesLayoutAndAdvancesRevision(){var settings=new BlogSettings();when(repository.findForUpdate()).thenReturn(Optional.of(settings));var saved=service.save(input(true,true,false,0,"https://github.com/KwonSeon"));assertEquals("내 블로그",saved.siteTitle());assertEquals(SettingsView.PostLayout.LIST,saved.postLayout());assertFalse(saved.showProjects());assertEquals(1L,saved.revision());verify(repository).save(settings);}
 @Test void staleEditorCannotOverwriteNewSettings(){var settings=new BlogSettings();settings.apply(input(true,true,true,0,""));when(repository.findForUpdate()).thenReturn(Optional.of(settings));assertThrows(BaseException.class,()->service.save(input(true,false,true,0,"")));assertEquals(1L,settings.getRevision());verify(repository,never()).save(any());}
 @Test void emptyHomepageIsRejected(){assertThrows(BaseException.class,()->service.save(input(false,false,false,0,"")));verifyNoInteractions(repository);}
 @Test void readsStoredSettings(){var settings=new BlogSettings();settings.apply(input(false,true,false,0,""));when(repository.findById(1L)).thenReturn(Optional.of(settings));assertFalse(service.read().showHero());assertEquals("",service.read().githubUrl());}
 @Test void defaultsRemainReadableBeforeInitialization(){assertEquals("s-nowk",service.read().siteTitle());}
 @Test void rejectsUnsafeGithubLinksAndAcceptsOptionalLink(){try(var factory=Validation.buildDefaultValidatorFactory()){var validator=factory.getValidator();for(String url:new String[]{"javascript:alert(1)","https://github.com.evil.test/user","https://user:pass@github.com/user","http://github.com/user"})assertFalse(validator.validate(input(true,true,true,0,url)).isEmpty());assertTrue(validator.validate(input(true,true,true,0,"https://github.com/KwonSeon/blog")).isEmpty());assertTrue(validator.validate(input(true,true,true,0,"")).isEmpty());}}
}
