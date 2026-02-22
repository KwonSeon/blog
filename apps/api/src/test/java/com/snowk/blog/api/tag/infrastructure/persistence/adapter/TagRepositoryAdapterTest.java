package com.snowk.blog.api.tag.infrastructure.persistence.adapter;

import static org.mockito.Mockito.verify;

import com.snowk.blog.api.tag.domain.entity.Tag;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import com.snowk.blog.api.tag.infrastructure.persistence.jpa.TagJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagRepositoryAdapterTest {

    @Mock
    private TagJpaRepository tagJpaRepository;

    @InjectMocks
    private TagRepositoryAdapter tagRepositoryAdapter;

    @Test
    @DisplayName("태그 저장은 JPA 리포지토리에 위임된다")
    void save_delegatesToJpaRepository() {
        Tag tag = org.mockito.Mockito.mock(Tag.class);

        tagRepositoryAdapter.save(tag);

        verify(tagJpaRepository).save(tag);
    }

    @Test
    @DisplayName("태그 ID 조회는 JPA 리포지토리에 위임된다")
    void findById_delegatesToJpaRepository() {
        tagRepositoryAdapter.findById(1L);

        verify(tagJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("태그 스코프/슬러그 조회는 JPA 리포지토리에 위임된다")
    void findByScopeAndSlug_delegatesToJpaRepository() {
        tagRepositoryAdapter.findByScopeAndSlug(TagScope.POST, "java");

        verify(tagJpaRepository).findByScopeAndSlug(TagScope.POST, "java");
    }
}
