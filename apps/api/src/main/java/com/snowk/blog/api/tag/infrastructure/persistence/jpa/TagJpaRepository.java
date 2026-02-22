package com.snowk.blog.api.tag.infrastructure.persistence.jpa;

import com.snowk.blog.api.tag.domain.entity.Tag;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByScopeAndSlug(TagScope scope, String slug);
}
