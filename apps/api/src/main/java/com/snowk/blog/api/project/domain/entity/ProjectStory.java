package com.snowk.blog.api.project.domain.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity @Table(name="project_stories") @Getter @NoArgsConstructor
public class ProjectStory {
 @Id @Column(name="project_id") private Long projectId;
 @Column(name="content_md",nullable=false,columnDefinition="LONGTEXT") private String contentMd;
 public ProjectStory(Long projectId,String contentMd){this.projectId=projectId;this.contentMd=contentMd;}
}
