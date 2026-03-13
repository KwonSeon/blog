package com.snowk.blog.api.project.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.exception.GlobalExceptionHandler;
import com.snowk.blog.api.project.application.port.in.GetPublicProjectUseCase;
import com.snowk.blog.api.project.application.port.in.ListPublicProjectsUseCase;
import com.snowk.blog.api.project.application.query.GetPublicProjectQuery;
import com.snowk.blog.api.project.application.query.ListPublicProjectsQuery;
import com.snowk.blog.api.project.application.result.GetPublicProjectResult;
import com.snowk.blog.api.project.application.result.ListPublicProjectsResult;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PublicProjectControllerTest {

    @Mock
    private GetPublicProjectUseCase getPublicProjectUseCase;

    @Mock
    private ListPublicProjectsUseCase listPublicProjectsUseCase;

    @InjectMocks
    private PublicProjectController publicProjectController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicProjectController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new JacksonJsonHttpMessageConverter())
            .build();
    }

    @Test
    @DisplayName("공개 프로젝트 목록 조회가 성공하면 200과 content/totalCount 응답을 반환한다")
    void listProjects_returns200() throws Exception {
        ArgumentCaptor<ListPublicProjectsQuery> queryCaptor = ArgumentCaptor.forClass(ListPublicProjectsQuery.class);

        when(listPublicProjectsUseCase.listProjects(queryCaptor.capture()))
            .thenReturn(new ListPublicProjectsResult(
                List.of(
                    new ListPublicProjectsResult.Item(
                        3L,
                        "newer-project",
                        "Newer Project",
                        "요약",
                        "https://service.example",
                        30L,
                        LocalDateTime.of(2026, 3, 8, 12, 0)
                    )
                ),
                1
            ));

        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.content[0].projectId").value(3))
            .andExpect(jsonPath("$.content[0].slug").value("newer-project"));

        assertThat(queryCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("공개 프로젝트 상세 조회가 성공하면 200과 상세 응답을 반환한다")
    void getProject_returns200() throws Exception {
        ArgumentCaptor<GetPublicProjectQuery> queryCaptor = ArgumentCaptor.forClass(GetPublicProjectQuery.class);

        when(getPublicProjectUseCase.getProject(queryCaptor.capture()))
            .thenReturn(new GetPublicProjectResult(
                1L,
                "snowk-blog",
                "Snowk Blog",
                "개인 블로그 프로젝트",
                "https://blog.s-nowk.com",
                "https://github.com/s-nowk/blog",
                10L,
                LocalDateTime.of(2026, 3, 8, 12, 0)
            ));

        mockMvc.perform(get("/api/projects/snowk-blog"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(1))
            .andExpect(jsonPath("$.slug").value("snowk-blog"))
            .andExpect(jsonPath("$.repoUrl").value("https://github.com/s-nowk/blog"));

        assertThat(queryCaptor.getValue().slug()).isEqualTo("snowk-blog");
    }

    @Test
    @DisplayName("공개 대상이 아닌 프로젝트를 조회하면 404 응답을 반환한다")
    void getProject_returns404_whenProjectIsHidden() throws Exception {
        when(getPublicProjectUseCase.getProject(any(GetPublicProjectQuery.class)))
            .thenThrow(new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));

        mockMvc.perform(get("/api/projects/hidden-project"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROJECT4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
