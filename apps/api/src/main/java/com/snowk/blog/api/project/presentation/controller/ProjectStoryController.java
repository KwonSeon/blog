package com.snowk.blog.api.project.presentation.controller;
import com.snowk.blog.api.project.application.service.ProjectStoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequiredArgsConstructor
public class ProjectStoryController {
 private final ProjectStoryService service;
 public record Story(@NotNull @Size(max=100000) String contentMd) {}
 @GetMapping("/api/admin/projects/{id}/story") public Story admin(@PathVariable Long id){return new Story(service.read(id));}
 @PutMapping("/api/admin/projects/{id}/story") public Story save(@PathVariable Long id,@Valid @RequestBody Story story){service.save(id,story.contentMd());return story;}
 @GetMapping("/api/public/projects/{slug}/story") public Story visible(@PathVariable String slug){return new Story(service.readPublic(slug));}
}
