package com.snowk.blog.api.post.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.snowk.blog.api.global.exception.BaseException;
import com.snowk.blog.api.global.exception.GlobalExceptionHandler;
import com.snowk.blog.api.post.application.port.in.GetPublicPostUseCase;
import com.snowk.blog.api.post.application.port.in.ListPublicPostsUseCase;
import com.snowk.blog.api.post.application.query.GetPublicPostQuery;
import com.snowk.blog.api.post.application.query.ListPublicPostsQuery;
import com.snowk.blog.api.post.application.result.GetPublicPostResult;
import com.snowk.blog.api.post.application.result.ListPublicPostsResult;
import com.snowk.blog.api.post.domain.error.PostErrorStatus;
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
class PublicPostControllerTest {

    @Mock
    private GetPublicPostUseCase getPublicPostUseCase;

    @Mock
    private ListPublicPostsUseCase listPublicPostsUseCase;

    @InjectMocks
    private PublicPostController publicPostController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicPostController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new JacksonJsonHttpMessageConverter())
            .build();
    }

    @Test
    @DisplayName("공개 게시글 목록 조회가 성공하면 200과 content/totalCount 응답을 반환한다")
    void listPosts_returns200() throws Exception {
        ArgumentCaptor<ListPublicPostsQuery> queryCaptor = ArgumentCaptor.forClass(ListPublicPostsQuery.class);

        when(listPublicPostsUseCase.listPosts(queryCaptor.capture()))
            .thenReturn(new ListPublicPostsResult(
                List.of(
                    new ListPublicPostsResult.Item(
                        3L,
                        "newer-post",
                        "Newer Post",
                        "요약",
                        "ko",
                        30L,
                        LocalDateTime.of(2026, 3, 8, 12, 0),
                        LocalDateTime.of(2026, 3, 8, 11, 0),
                        LocalDateTime.of(2026, 3, 8, 12, 30)
                    )
                ),
                1
            ));

        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.content[0].postId").value(3))
            .andExpect(jsonPath("$.content[0].slug").value("newer-post"))
            .andExpect(jsonPath("$.content[0].createdAt").exists())
            .andExpect(jsonPath("$.content[0].updatedAt").exists());

        assertThat(queryCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("공개 게시글 상세 조회가 성공하면 200과 상세 응답을 반환한다")
    void getPost_returns200() throws Exception {
        ArgumentCaptor<GetPublicPostQuery> queryCaptor = ArgumentCaptor.forClass(GetPublicPostQuery.class);

        when(getPublicPostUseCase.getPost(queryCaptor.capture()))
            .thenReturn(new GetPublicPostResult(
                1L,
                "snowk-blog",
                "Snowk Blog",
                "개인 블로그 소개",
                "# hello",
                "ko",
                10L,
                LocalDateTime.of(2026, 3, 8, 12, 0),
                LocalDateTime.of(2026, 3, 8, 11, 0),
                LocalDateTime.of(2026, 3, 8, 12, 30)
            ));

        mockMvc.perform(get("/api/posts/snowk-blog"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.postId").value(1))
            .andExpect(jsonPath("$.slug").value("snowk-blog"))
            .andExpect(jsonPath("$.contentMd").value("# hello"))
            .andExpect(jsonPath("$.updatedAt").exists());

        assertThat(queryCaptor.getValue().slug()).isEqualTo("snowk-blog");
    }

    @Test
    @DisplayName("공개 대상이 아닌 게시글을 조회하면 404 응답을 반환한다")
    void getPost_returns404_whenPostIsHidden() throws Exception {
        when(getPublicPostUseCase.getPost(any(GetPublicPostQuery.class)))
            .thenThrow(new BaseException(PostErrorStatus.POST_NOT_FOUND));

        mockMvc.perform(get("/api/posts/hidden-post"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("POST4041"))
            .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
