package com.snowk.blog.api.tag.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.snowk.blog.api.support.ReflectionEntityFactory;
import com.snowk.blog.api.tag.domain.entity.Tag;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TagJpaRepositoryTest {

    @Autowired
    private TagJpaRepository tagJpaRepository;

    @Test
    @DisplayName("스코프와 슬러그로 태그를 조회할 수 있다")
    void findByScopeAndSlug_returnsSavedTag() {
        Tag tag = createTag(TagScope.POST, "spring", "Spring");

        tagJpaRepository.save(tag);

        assertThat(tagJpaRepository.findByScopeAndSlug(TagScope.POST, "spring")).isPresent();
    }

    @Test
    @DisplayName("같은 스코프와 슬러그 조합의 태그는 저장할 수 없다")
    void save_duplicateScopeAndSlug_throwsDataIntegrityViolation() {
        tagJpaRepository.saveAndFlush(createTag(TagScope.POST, "backend", "Backend"));

        assertThatThrownBy(() ->
            tagJpaRepository.saveAndFlush(createTag(TagScope.POST, "backend", "Backend2")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("스코프가 없으면 태그를 저장할 수 없다")
    void save_nullScope_throwsDataIntegrityViolation() {
        Tag tag = createTag(TagScope.POST, "no-scope", "No Scope");
        ReflectionEntityFactory.setField(tag, "scope", null);

        assertThatThrownBy(() -> tagJpaRepository.saveAndFlush(tag))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    private Tag createTag(TagScope scope, String slug, String name) {
        Tag tag = ReflectionEntityFactory.instantiate(Tag.class);
        ReflectionEntityFactory.setField(tag, "scope", scope);
        ReflectionEntityFactory.setField(tag, "slug", slug);
        ReflectionEntityFactory.setField(tag, "name", name);
        return tag;
    }
}
