package com.snowk.blog.api.tag.application.port.out;

import com.snowk.blog.api.tag.domain.entity.Tag;
import com.snowk.blog.api.tag.domain.enumtype.TagScope;
import java.util.Optional;

public interface TagRepositoryPort {

    Tag save(Tag tag);

    Optional<Tag> findById(Long tagId);

    Optional<Tag> findByScopeAndSlug(TagScope scope, String slug);
}
