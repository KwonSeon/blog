package com.snowk.blog.api.settings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.error.CommonErrorStatus;
@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class BlogSettingsService {
 private final BlogSettingsRepository repository;
 public SettingsView read(){return repository.findById(1L).orElseGet(BlogSettings::new).view();}
 @Transactional public SettingsView save(SettingsView input){
  if(!input.showHero()&&!input.showPosts()&&!input.showProjects())throw new BaseException(CommonErrorStatus.BAD_REQUEST);
  var settings=repository.findForUpdate().orElseThrow(()->new BaseException(CommonErrorStatus.RESOURCE_NOT_FOUND));
  if(input.revision()!=settings.getRevision())throw new BaseException(CommonErrorStatus.CONFLICT);
  settings.apply(input);repository.save(settings);return settings.view();
 }
}
