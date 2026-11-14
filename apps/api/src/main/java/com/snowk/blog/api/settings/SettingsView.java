package com.snowk.blog.api.settings;
import jakarta.validation.constraints.*;
public record SettingsView(
 @NotBlank @Size(max=80) String siteTitle,
 @NotBlank @Size(max=160) String heroTitle,
 @NotNull @Size(max=600) String heroDescription,
 @NotNull @Size(max=500) @Pattern(regexp="^$|^https://github\\.com/[A-Za-z0-9_./-]+$") String githubUrl,
 @NotNull HomeOrder homeOrder,
 @NotNull PostLayout postLayout,
 @NotNull ContentWidth contentWidth,
 @NotNull Boolean showHero,
 @NotNull Boolean showPosts,
 @NotNull Boolean showProjects,
 @NotNull @PositiveOrZero Long revision
){
 public enum HomeOrder {POSTS_FIRST,PROJECTS_FIRST}
 public enum PostLayout {GRID,LIST}
 public enum ContentWidth {STANDARD,WIDE}
}
