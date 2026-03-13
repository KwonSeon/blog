package com.snowk.blog.api.post.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snowk.blog.api.common.domain.enumtype.Visibility;
import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.exception.GlobalExceptionHandler;
import com.snowk.blog.api.global.security.authentication.AuthenticatedUserAuthenticationToken;
import com.snowk.blog.api.global.security.principal.AuthenticatedUser;
import com.snowk.blog.api.post.application.command.ChangePostStatusCommand;
import com.snowk.blog.api.post.application.command.CreatePostCommand;
import com.snowk.blog.api.post.application.command.UpdatePostCommand;
import com.snowk.blog.api.post.application.port.in.ChangePostStatusUseCase;
import com.snowk.blog.api.post.application.port.in.CreatePostUseCase;
import com.snowk.blog.api.post.application.port.in.DeletePostUseCase;
import com.snowk.blog.api.post.application.port.in.GetPostUseCase;
import com.snowk.blog.api.post.application.port.in.ListPostsUseCase;
import com.snowk.blog.api.post.application.port.in.UpdatePostUseCase;
import com.snowk.blog.api.post.application.query.GetPostQuery;
import com.snowk.blog.api.post.application.query.ListPostsQuery;
import com.snowk.blog.api.post.application.result.ChangePostStatusResult;
import com.snowk.blog.api.post.application.result.CreatePostResult;
import com.snowk.blog.api.post.application.result.GetPostResult;
import com.snowk.blog.api.post.application.result.ListPostsResult;
import com.snowk.blog.api.post.application.result.UpdatePostResult;
import com.snowk.blog.api.post.domain.enumtype.PostStatus;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class AdminPostControllerTest {

    @Mock
    private CreatePostUseCase createPostUseCase;

    @Mock
    private GetPostUseCase getPostUseCase;

    @Mock
    private ListPostsUseCase listPostsUseCase;

    @Mock
    private UpdatePostUseCase updatePostUseCase;

    @Mock
    private ChangePostStatusUseCase changePostStatusUseCase;

    @Mock
    private DeletePostUseCase deletePostUseCase;

    @InjectMocks
    private AdminPostController adminPostController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(adminPostController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
            .setValidator(validator)
            .setMessageConverters(new JacksonJsonHttpMessageConverter())
            .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("게시글 생성 요청이 성공하면 201과 응답 본문을 반환한다")
    void createPost_returnsCreatedResponse_whenRequestIsValid() throws Exception {
        setJwtAuthentication("1");
        ArgumentCaptor<CreatePostCommand> commandCaptor = ArgumentCaptor.forClass(CreatePostCommand.class);

        when(createPostUseCase.createPost(commandCaptor.capture()))
            .thenReturn(new CreatePostResult(
                1L,
                "hello-world",
                "Hello World",
                "요약",
                "# content",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                "ko",
                10L,
                1L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(
                post("/api/admin/posts")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "hello-world",
                          "title": "Hello World",
                          "excerpt": "요약",
                          "contentMd": "# content",
                          "visibility": "PUBLIC",
                          "status": "PUBLISHED",
                          "lang": "ko",
                          "coverMediaAssetId": 10
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.postId").value(1))
            .andExpect(jsonPath("$.slug").value("hello-world"))
            .andExpect(jsonPath("$.title").value("Hello World"))
            .andExpect(jsonPath("$.status").value("PUBLISHED"))
            .andExpect(jsonPath("$.authorUserId").value(1))
            .andExpect(jsonPath("$.publishedAt").exists());

        assertThat(commandCaptor.getValue().slug()).isEqualTo("hello-world");
        assertThat(commandCaptor.getValue().title()).isEqualTo("Hello World");
        assertThat(commandCaptor.getValue().excerpt()).isEqualTo("요약");
        assertThat(commandCaptor.getValue().contentMd()).isEqualTo("# content");
        assertThat(commandCaptor.getValue().visibility()).isEqualTo(Visibility.PUBLIC);
        assertThat(commandCaptor.getValue().status()).isEqualTo(PostStatus.PUBLISHED);
        assertThat(commandCaptor.getValue().lang()).isEqualTo("ko");
        assertThat(commandCaptor.getValue().coverMediaAssetId()).isEqualTo(10L);
        assertThat(commandCaptor.getValue().authorUserId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("게시글 생성 요청 본문이 유효하지 않으면 400 응답을 반환한다")
    void createPost_returnsBadRequest_whenRequestBodyIsInvalid() throws Exception {
        setJwtAuthentication("1");
        mockMvc.perform(
                post("/api/admin/posts")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "",
                          "title": "Hello World",
                          "excerpt": "요약",
                          "contentMd": "# content",
                          "visibility": "PUBLIC",
                          "status": "PUBLISHED",
                          "lang": "ko"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("COMMON400"))
            .andExpect(jsonPath("$.isSuccess").value(false));

    }

    @Test
    @DisplayName("slug가 중복되면 생성에서 409 응답을 반환한다")
    void createPost_returnsConflict_whenUseCaseThrowsDuplicateSlugException() throws Exception {
        setJwtAuthentication("1");
        when(createPostUseCase.createPost(any(CreatePostCommand.class)))
            .thenThrow(new BaseException(PostErrorStatus.DUPLICATE_POST_SLUG));

        mockMvc.perform(
                post("/api/admin/posts")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "hello-world",
                          "title": "Hello World",
                          "excerpt": "요약",
                          "contentMd": "# content",
                          "visibility": "PUBLIC",
                          "status": "PUBLISHED",
                          "lang": "ko"
                        }
                        """)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("POST4091"))
            .andExpect(jsonPath("$.isSuccess").value(false));

    }

    @Test
    @DisplayName("게시글 상세 조회가 성공하면 200과 상세 응답을 반환한다")
    void getPost_returnsDetailResponse_whenPostExists() throws Exception {
        ArgumentCaptor<GetPostQuery> queryCaptor = ArgumentCaptor.forClass(GetPostQuery.class);

        when(getPostUseCase.getPost(queryCaptor.capture()))
            .thenReturn(new GetPostResult(
                1L,
                "hello-world",
                "Hello World",
                "요약",
                "# content",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                "ko",
                10L,
                1L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(get("/api/admin/posts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").value(1))
            .andExpect(jsonPath("$.slug").value("hello-world"))
            .andExpect(jsonPath("$.contentMd").value("# content"));

        assertThat(queryCaptor.getValue().postId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("게시글이 없으면 상세 조회에서 404 응답을 반환한다")
    void getPost_returnsNotFound_whenUseCaseThrowsPostNotFoundException() throws Exception {
        when(getPostUseCase.getPost(any(GetPostQuery.class)))
            .thenThrow(new BaseException(PostErrorStatus.POST_NOT_FOUND));

        mockMvc.perform(get("/api/admin/posts/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("POST4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("게시글 목록 조회가 성공하면 200과 content/totalCount 응답을 반환한다")
    void listPosts_returnsListResponse_whenPostsExist() throws Exception {
        ArgumentCaptor<ListPostsQuery> queryCaptor = ArgumentCaptor.forClass(ListPostsQuery.class);

        when(listPostsUseCase.listPosts(queryCaptor.capture()))
            .thenReturn(new ListPostsResult(
                java.util.List.of(
                    new ListPostsResult.Item(
                        3L,
                        "newer-post-high-id",
                        "Newer Post High Id",
                        "요약3",
                        Visibility.PUBLIC,
                        PostStatus.PUBLISHED,
                        "ko",
                        13L,
                        1L,
                        LocalDateTime.of(2026, 3, 8, 12, 0),
                        LocalDateTime.of(2026, 3, 8, 12, 1),
                        LocalDateTime.of(2026, 3, 8, 12, 2)
                    ),
                    new ListPostsResult.Item(
                        2L,
                        "newer-post-low-id",
                        "Newer Post Low Id",
                        "요약2",
                        Visibility.PRIVATE,
                        PostStatus.DRAFT,
                        "en",
                        12L,
                        2L,
                        null,
                        LocalDateTime.of(2026, 3, 8, 11, 0),
                        LocalDateTime.of(2026, 3, 8, 11, 1)
                    )
                ),
                2
            ));

        mockMvc.perform(get("/api/admin/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount").value(2))
            .andExpect(jsonPath("$.content[0].postId").value(3))
            .andExpect(jsonPath("$.content[0].slug").value("newer-post-high-id"))
            .andExpect(jsonPath("$.content[1].postId").value(2))
            .andExpect(jsonPath("$.content[1].status").value("DRAFT"));

        assertThat(queryCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("게시글 수정 요청이 성공하면 200과 응답 본문을 반환한다")
    void updatePost_returnsUpdatedResponse_whenRequestIsValid() throws Exception {
        ArgumentCaptor<UpdatePostCommand> commandCaptor = ArgumentCaptor.forClass(UpdatePostCommand.class);

        when(updatePostUseCase.updatePost(commandCaptor.capture()))
            .thenReturn(new UpdatePostResult(
                1L,
                "hello-world",
                "Hello World V2",
                "수정된 요약",
                "## updated",
                Visibility.PRIVATE,
                PostStatus.PUBLISHED,
                "en",
                20L,
                1L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(
                put("/api/admin/posts/1")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "hello-world",
                          "title": "Hello World V2",
                          "excerpt": "수정된 요약",
                          "contentMd": "## updated",
                          "visibility": "PRIVATE",
                          "lang": "en",
                          "coverMediaAssetId": 20
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").value(1))
            .andExpect(jsonPath("$.title").value("Hello World V2"))
            .andExpect(jsonPath("$.visibility").value("PRIVATE"))
            .andExpect(jsonPath("$.lang").value("en"));

        assertThat(commandCaptor.getValue().postId()).isEqualTo(1L);
        assertThat(commandCaptor.getValue().title()).isEqualTo("Hello World V2");
        assertThat(commandCaptor.getValue().contentMd()).isEqualTo("## updated");
        assertThat(commandCaptor.getValue().visibility()).isEqualTo(Visibility.PRIVATE);
    }

    @Test
    @DisplayName("게시글 수정 요청 본문이 유효하지 않으면 400 응답을 반환한다")
    void updatePost_returnsBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(
                put("/api/admin/posts/1")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "",
                          "title": "Hello World V2",
                          "excerpt": "수정된 요약",
                          "contentMd": "## updated",
                          "visibility": "PRIVATE",
                          "lang": "en"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("COMMON400"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("수정 대상 게시글이 없으면 404 응답을 반환한다")
    void updatePost_returnsNotFound_whenUseCaseThrowsPostNotFoundException() throws Exception {
        when(updatePostUseCase.updatePost(any(UpdatePostCommand.class)))
            .thenThrow(new BaseException(PostErrorStatus.POST_NOT_FOUND));

        mockMvc.perform(
                put("/api/admin/posts/999")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "hello-world",
                          "title": "Hello World V2",
                          "excerpt": "수정된 요약",
                          "contentMd": "## updated",
                          "visibility": "PRIVATE",
                          "lang": "en"
                        }
                        """)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("POST4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("slug가 중복되면 수정에서 409 응답을 반환한다")
    void updatePost_returnsConflict_whenUseCaseThrowsDuplicateSlugException() throws Exception {
        when(updatePostUseCase.updatePost(any(UpdatePostCommand.class)))
            .thenThrow(new BaseException(PostErrorStatus.DUPLICATE_POST_SLUG));

        mockMvc.perform(
                put("/api/admin/posts/1")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "slug": "hello-world-v2",
                          "title": "Hello World V2",
                          "excerpt": "수정된 요약",
                          "contentMd": "## updated",
                          "visibility": "PRIVATE",
                          "lang": "en"
                        }
                        """)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("POST4091"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("게시글 상태 변경 요청이 성공하면 200과 응답 본문을 반환한다")
    void changePostStatus_returnsUpdatedResponse_whenRequestIsValid() throws Exception {
        ArgumentCaptor<ChangePostStatusCommand> commandCaptor = ArgumentCaptor.forClass(ChangePostStatusCommand.class);

        when(changePostStatusUseCase.changePostStatus(commandCaptor.capture()))
            .thenReturn(new ChangePostStatusResult(
                1L,
                "hello-world",
                "Hello World",
                "요약",
                "# content",
                Visibility.PUBLIC,
                PostStatus.PUBLISHED,
                "ko",
                10L,
                1L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 12, 1),
                LocalDateTime.of(2026, 3, 8, 12, 2)
            ));

        mockMvc.perform(
                patch("/api/admin/posts/1/status")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "status": "PUBLISHED"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").value(1))
            .andExpect(jsonPath("$.status").value("PUBLISHED"))
            .andExpect(jsonPath("$.publishedAt").exists());

        assertThat(commandCaptor.getValue().postId()).isEqualTo(1L);
        assertThat(commandCaptor.getValue().status()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @DisplayName("게시글 상태 변경 요청 본문이 유효하지 않으면 400 응답을 반환한다")
    void changePostStatus_returnsBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(
                patch("/api/admin/posts/1/status")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("COMMON400"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("상태 변경 대상 게시글이 없으면 404 응답을 반환한다")
    void changePostStatus_returnsNotFound_whenUseCaseThrowsPostNotFoundException() throws Exception {
        when(changePostStatusUseCase.changePostStatus(any(ChangePostStatusCommand.class)))
            .thenThrow(new BaseException(PostErrorStatus.POST_NOT_FOUND));

        mockMvc.perform(
                patch("/api/admin/posts/999/status")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "status": "DRAFT"
                        }
                        """)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("POST4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    @DisplayName("게시글 삭제 요청이 성공하면 204 응답을 반환한다")
    void deletePost_returnsNoContent_whenPostExists() throws Exception {
        mockMvc.perform(delete("/api/admin/posts/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("삭제 대상 게시글이 없으면 404 응답을 반환한다")
    void deletePost_returnsNotFound_whenUseCaseThrowsPostNotFoundException() throws Exception {
        doThrow(new BaseException(PostErrorStatus.POST_NOT_FOUND))
            .when(deletePostUseCase)
            .deletePost(eq(999L));

        mockMvc.perform(delete("/api/admin/posts/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("POST4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }

    private void setJwtAuthentication(String subject) {
        Jwt jwt = Jwt.withTokenValue("access-token")
            .header("alg", "HS256")
            .subject(subject)
            .claim("userId", Long.parseLong(subject))
            .claim("username", "admin")
            .claim("roles", java.util.List.of("ROLE_ADMIN"))
            .build();
        AuthenticatedUser principal = new AuthenticatedUser(
            subject,
            Long.parseLong(subject),
            "admin",
            java.util.List.of("ROLE_ADMIN")
        );
        SecurityContextHolder.getContext().setAuthentication(
            new AuthenticatedUserAuthenticationToken(
                principal,
                jwt,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            )
        );
    }
}
