package com.snowk.blog.api.settings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity @Table(name="blog_settings") @Getter @NoArgsConstructor
public class BlogSettings {
 @Id private Long id=1L;
 @Column(name="site_title",nullable=false,length=80) private String siteTitle="s-nowk";
 @Column(name="hero_title",nullable=false,length=160) private String heroTitle="만들고, 배우고,\n기록합니다.";
 @Column(name="hero_description",nullable=false,length=600) private String heroDescription="직접 만드는 서비스와 그 과정에서 만난 문제들. 개발의 생각과 경험을 차곡차곡 남기는 공간입니다.";
 @Column(name="github_url",nullable=false,length=500) private String githubUrl="https://github.com/KwonSeon";
 @Enumerated(EnumType.STRING) @Column(name="home_order",nullable=false,length=30) private SettingsView.HomeOrder homeOrder=SettingsView.HomeOrder.POSTS_FIRST;
 @Enumerated(EnumType.STRING) @Column(name="post_layout",nullable=false,length=20) private SettingsView.PostLayout postLayout=SettingsView.PostLayout.GRID;
 @Enumerated(EnumType.STRING) @Column(name="content_width",nullable=false,length=20) private SettingsView.ContentWidth contentWidth=SettingsView.ContentWidth.STANDARD;
 @Column(name="show_hero",nullable=false) private boolean showHero=true;
 @Column(name="show_posts",nullable=false) private boolean showPosts=true;
 @Column(name="show_projects",nullable=false) private boolean showProjects=true;
 @Column(nullable=false) private long revision=0;
 public void apply(SettingsView v){siteTitle=v.siteTitle().trim();heroTitle=v.heroTitle().trim();heroDescription=v.heroDescription().trim();githubUrl=v.githubUrl().trim();homeOrder=v.homeOrder();postLayout=v.postLayout();contentWidth=v.contentWidth();showHero=v.showHero();showPosts=v.showPosts();showProjects=v.showProjects();revision++;}
 public SettingsView view(){return new SettingsView(siteTitle,heroTitle,heroDescription,githubUrl,homeOrder,postLayout,contentWidth,showHero,showPosts,showProjects,revision);}
}
