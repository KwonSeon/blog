package com.snowk.blog.api.project.presentation.controller;

import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.exception.GlobalExceptionHandler;
import com.snowk.blog.api.project.application.command.CreateProjectCommand;
import com.snowk.blog.api.project.application.port.in.CreateProjectUseCase;
import com.snowk.blog.api.project.application.port.in.DeleteProjectUseCase;
import com.snowk.blog.api.project.application.port.in.GetProjectUseCase;
import com.snowk.blog.api.project.application.port.in.ListProjectsUseCase;
import com.snowk.blog.api.project.application.port.in.UpdateProjectUseCase;
import com.snowk.blog.api.project.application.command.UpdateProjectCommand;
import com.snowk.blog.api.project.application.query.GetProjectQuery;
import com.snowk.blog.api.project.application.query.ListProjectsQuery;
import com.snowk.blog.api.project.application.result.CreateProjectResult;
import com.snowk.blog.api.project.application.result.GetProjectResult;
import com.snowk.blog.api.project.application.result.ListProjectsResult;
import com.snowk.blog.api.project.application.result.UpdateProjectResult;
import com.snowk.blog.api.project.domain.error.ProjectErrorStatus;
import com.snowk.blog.api.project.domain.enumtype.ProjectStatus;
import com.snowk.blog.api.shared.domain.enumtype.Visibility;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminProjectControllerTest {

    @Mock
    private CreateProjectUseCase createProjectUseCase;

    @Mock
    private GetProjectUseCase getProjectUseCase;

    @Mock
    private ListProjectsUseCase listProjectsUseCase;

    @Mock
    private UpdateProjectUseCase updateProjectUseCase;

    @Mock
    private DeleteProjectUseCase deleteProjectUseCase;

    @InjectMocks
    private AdminProjectController adminProjectController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(adminProjectController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .setMessageConverters(new JacksonJsonHttpMessageConverter())
            .build();
    }

    @Test
    @DisplayName("프로젝트 생성 요청이 성공하면 201과 응답 본문을 반환한다")
    void createProject_returnsCreatedResponse_whenRequestIsValid() throws Exception {
        ArgumentCaptor<CreateProjectCommand> commandCaptor = ArgumentCaptor.forClass(CreateProjectCommand.class);

        when(createProjectUseCase.createProject(commandCaptor.capture()))
            .thenReturn(new CreateProjectResult(
                1L,
                "snowk-blog",
                "Snowk Blog",
                "개인 블로그 프로젝트",
                "https://blog.s-nowk.com",
                "https://github.com/s-nowk/blog",
                Visibility.PUBLIC,
                ProjectStatus.ACTIVE,
                10L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(
                post("/api/admin/projects")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "snowk-blog",
                          "title": "Snowk Blog",
                          "summary": "개인 블로그 프로젝트",
                          "serviceUrl": "https://blog.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog",
                          "visibility": "PUBLIC",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 10
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.projectId").value(1))
            .andExpect(jsonPath("$.slug").value("snowk-blog"))
            .andExpect(jsonPath("$.title").value("Snowk Blog"))
            .andExpect(jsonPath("$.visibility").value("PUBLIC"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.publishedAt").exists())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());

        assertThat(commandCaptor.getValue().slug()).isEqualTo("snowk-blog");
        assertThat(commandCaptor.getValue().title()).isEqualTo("Snowk Blog");
        assertThat(commandCaptor.getValue().summary()).isEqualTo("개인 블로그 프로젝트");
        assertThat(commandCaptor.getValue().serviceUrl()).isEqualTo("https://blog.s-nowk.com");
        assertThat(commandCaptor.getValue().repoUrl()).isEqualTo("https://github.com/s-nowk/blog");
        assertThat(commandCaptor.getValue().visibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(commandCaptor.getValue().status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(commandCaptor.getValue().coverMediaAssetId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("프로젝트 생성 요청 본문이 유효하지 않으면 400 응답을 반환한다")
    void createProject_returnsBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(
                post("/api/admin/projects")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "",
                          "title": "Snowk Blog",
                          "summary": "개인 블로그 프로젝트",
                          "serviceUrl": "https://blog.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog",
                          "visibility": "PUBLIC",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 10
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("COMMON400"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("slug가 중복되면 409 응답을 반환한다")
    void createProject_returnsConflict_whenUseCaseThrowsDuplicateSlugException() throws Exception {
        when(createProjectUseCase.createProject(any(CreateProjectCommand.class)))
            .thenThrow(new BaseException(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG));

        mockMvc.perform(
                post("/api/admin/projects")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "snowk-blog",
                          "title": "Snowk Blog",
                          "summary": "개인 블로그 프로젝트",
                          "serviceUrl": "https://blog.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog",
                          "visibility": "PUBLIC",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 10
                        }
                        """)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("PROJECT4091"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("프로젝트 상세 조회가 성공하면 200과 상세 응답을 반환한다")
    void getProject_returnsDetailResponse_whenProjectExists() throws Exception {
        ArgumentCaptor<GetProjectQuery> queryCaptor = ArgumentCaptor.forClass(GetProjectQuery.class);

        when(getProjectUseCase.getProject(queryCaptor.capture()))
            .thenReturn(new GetProjectResult(
                1L,
                "snowk-blog",
                "Snowk Blog",
                "개인 블로그 프로젝트",
                "https://blog.s-nowk.com",
                "https://github.com/s-nowk/blog",
                Visibility.PUBLIC,
                ProjectStatus.ACTIVE,
                10L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(get("/api/admin/projects/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(1))
            .andExpect(jsonPath("$.slug").value("snowk-blog"))
            .andExpect(jsonPath("$.title").value("Snowk Blog"))
            .andExpect(jsonPath("$.visibility").value("PUBLIC"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.publishedAt").exists())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());

        assertThat(queryCaptor.getValue().projectId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("프로젝트가 없으면 상세 조회에서 404 응답을 반환한다")
    void getProject_returnsNotFound_whenUseCaseThrowsProjectNotFoundException() throws Exception {
        when(getProjectUseCase.getProject(any(GetProjectQuery.class)))
            .thenThrow(new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));

        mockMvc.perform(get("/api/admin/projects/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROJECT4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("프로젝트 목록 조회가 성공하면 200과 content/totalCount 응답을 반환한다")
    void listProjects_returnsListResponse_whenProjectsExist() throws Exception {
        ArgumentCaptor<ListProjectsQuery> queryCaptor = ArgumentCaptor.forClass(ListProjectsQuery.class);

        when(listProjectsUseCase.listProjects(queryCaptor.capture()))
            .thenReturn(new ListProjectsResult(
                java.util.List.of(
                    new ListProjectsResult.Item(
                        3L,
                        "newer-project-high-id",
                        "Newer Project High Id",
                        Visibility.PUBLIC,
                        ProjectStatus.ACTIVE,
                        LocalDateTime.of(2026, 3, 8, 12, 0),
                        LocalDateTime.of(2026, 3, 8, 12, 1),
                        LocalDateTime.of(2026, 3, 8, 12, 2)
                    ),
                    new ListProjectsResult.Item(
                        2L,
                        "newer-project-low-id",
                        "Newer Project Low Id",
                        Visibility.PUBLIC,
                        ProjectStatus.ACTIVE,
                        LocalDateTime.of(2026, 3, 8, 11, 0),
                        LocalDateTime.of(2026, 3, 8, 11, 1),
                        LocalDateTime.of(2026, 3, 8, 11, 2)
                    )
                ),
                2
            ));

        mockMvc.perform(get("/api/admin/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount").value(2))
            .andExpect(jsonPath("$.content[0].projectId").value(3))
            .andExpect(jsonPath("$.content[0].slug").value("newer-project-high-id"))
            .andExpect(jsonPath("$.content[1].projectId").value(2))
            .andExpect(jsonPath("$.content[1].slug").value("newer-project-low-id"));

        assertThat(queryCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("프로젝트 수정 요청이 성공하면 200과 응답 본문을 반환한다")
    void updateProject_returnsUpdatedResponse_whenRequestIsValid() throws Exception {
        ArgumentCaptor<UpdateProjectCommand> commandCaptor = ArgumentCaptor.forClass(UpdateProjectCommand.class);

        when(updateProjectUseCase.updateProject(commandCaptor.capture()))
            .thenReturn(new UpdateProjectResult(
                1L,
                "snowk-blog",
                "Snowk Blog V2",
                "수정된 프로젝트 요약",
                "https://blog-v2.s-nowk.com",
                "https://github.com/s-nowk/blog-v2",
                Visibility.PRIVATE,
                ProjectStatus.ACTIVE,
                20L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(
                put("/api/admin/projects/1")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "snowk-blog",
                          "title": "Snowk Blog V2",
                          "summary": "수정된 프로젝트 요약",
                          "serviceUrl": "https://blog-v2.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog-v2",
                          "visibility": "PRIVATE",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 20
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(1))
            .andExpect(jsonPath("$.slug").value("snowk-blog"))
            .andExpect(jsonPath("$.title").value("Snowk Blog V2"))
            .andExpect(jsonPath("$.visibility").value("PRIVATE"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.publishedAt").exists())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());

        assertThat(commandCaptor.getValue().projectId()).isEqualTo(1L);
        assertThat(commandCaptor.getValue().slug()).isEqualTo("snowk-blog");
        assertThat(commandCaptor.getValue().title()).isEqualTo("Snowk Blog V2");
        assertThat(commandCaptor.getValue().summary()).isEqualTo("수정된 프로젝트 요약");
        assertThat(commandCaptor.getValue().serviceUrl()).isEqualTo("https://blog-v2.s-nowk.com");
        assertThat(commandCaptor.getValue().repoUrl()).isEqualTo("https://github.com/s-nowk/blog-v2");
        assertThat(commandCaptor.getValue().visibility()).isEqualTo(Visibility.PRIVATE);
        assertThat(commandCaptor.getValue().status()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(commandCaptor.getValue().coverMediaAssetId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("프로젝트 수정 요청 본문이 유효하지 않으면 400 응답을 반환한다")
    void updateProject_returnsBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(
                put("/api/admin/projects/1")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "",
                          "title": "Snowk Blog V2",
                          "summary": "수정된 프로젝트 요약",
                          "serviceUrl": "https://blog-v2.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog-v2",
                          "visibility": "PRIVATE",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 20
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("COMMON400"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("수정 대상 프로젝트가 없으면 404 응답을 반환한다")
    void updateProject_returnsNotFound_whenUseCaseThrowsProjectNotFoundException() throws Exception {
        when(updateProjectUseCase.updateProject(any(UpdateProjectCommand.class)))
            .thenThrow(new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND));

        mockMvc.perform(
                put("/api/admin/projects/999")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "snowk-blog",
                          "title": "Snowk Blog V2",
                          "summary": "수정된 프로젝트 요약",
                          "serviceUrl": "https://blog-v2.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog-v2",
                          "visibility": "PRIVATE",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 20
                        }
                        """)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROJECT4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("slug가 중복되면 수정에서 409 응답을 반환한다")
    void updateProject_returnsConflict_whenUseCaseThrowsDuplicateSlugException() throws Exception {
        when(updateProjectUseCase.updateProject(any(UpdateProjectCommand.class)))
            .thenThrow(new BaseException(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG));

        mockMvc.perform(
                put("/api/admin/projects/1")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "snowk-blog",
                          "title": "Snowk Blog V2",
                          "summary": "수정된 프로젝트 요약",
                          "serviceUrl": "https://blog-v2.s-nowk.com",
                          "repoUrl": "https://github.com/s-nowk/blog-v2",
                          "visibility": "PRIVATE",
                          "status": "ACTIVE",
                          "coverMediaAssetId": 20
                        }
                        """)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("PROJECT4091"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("프로젝트 삭제 요청이 성공하면 204 응답을 반환한다")
    void deleteProject_returnsNoContent_whenProjectExists() throws Exception {
        mockMvc.perform(delete("/api/admin/projects/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("삭제 대상 프로젝트가 없으면 404 응답을 반환한다")
    void deleteProject_returnsNotFound_whenUseCaseThrowsProjectNotFoundException() throws Exception {
        doThrow(new BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND))
            .when(deleteProjectUseCase)
            .deleteProject(eq(999L));

        mockMvc.perform(delete("/api/admin/projects/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROJECT4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
